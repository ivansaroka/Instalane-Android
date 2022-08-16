package com.infinix.instalane.utils

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.ApiErrorParser

fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun Context.showErrorMessage(throwable: Throwable?) =
    showMessage(ApiErrorParser.parseError(this, throwable))

fun Context.showMessage(message: String?) {
    if (message != null) Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.margins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        left?.let { leftMargin = it }
        top?.let { topMargin = it }
        right?.let { rightMargin = it }
        bottom?.let { bottomMargin = it }
    }
}

fun EditText.listenerAfterTextChanged(funToUse: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            funToUse.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun Context.getDrawableByUrl(
    url: String?,
    size: Int? = null,
    listener: (drawable: Drawable) -> Unit
) {
    var requestBuilder = Glide.with(this).asDrawable().load(url)
    if (size != null) requestBuilder = requestBuilder.override(size)
    requestBuilder.into(object : CustomTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            listener.invoke(resource)
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    })
}

fun ImageView.loadImage(url: String?, placeHolder: Int, circleCrop: Boolean = false) {
    var requestOptions = RequestOptions().placeholder(placeHolder).override(400)
    requestOptions = if (circleCrop) requestOptions.circleCrop() else requestOptions
    Glide.with(context).load(url).apply(requestOptions).into(this)
}

fun ImageView.loadImage(resourceId: Int, circleCrop: Boolean = false) {
    var requestOptions = RequestOptions().override(400)
    requestOptions = if (circleCrop) requestOptions.circleCrop() else requestOptions
    Glide.with(context).load(resourceId).apply(requestOptions).into(this)
}

fun ImageView.loadImage(resourceId: Int, requestOptions: RequestOptions) {
    Glide.with(context).load(resourceId).apply(requestOptions).into(this)
}

fun ImageView.loadImage(url: String?, requestOptions: RequestOptions) {
    Glide.with(context).load(url).apply(requestOptions).into(this)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun ViewGroup.inflate(layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun formatTimeUSA(strDate: String?, formatOrigin: String?): String {
    val sdfOrigin = SimpleDateFormat(formatOrigin, Locale.getDefault())
    val sdfDestiny = SimpleDateFormat("H:mm a", Locale.getDefault())
    return sdfDestiny.format(sdfOrigin.parse(strDate))
}

fun formatDateUSA(strDate: String?, formatOrigin: String?): String {
    val sdfOrigin = SimpleDateFormat(formatOrigin, Locale.getDefault())
    val sdfDestiny = SimpleDateFormat("M-d-yyyy", Locale.getDefault())
    return sdfDestiny.format(sdfOrigin.parse(strDate))
}

fun formatDateString(
    strDate: String?,
    formatOrigin: String?,
    formatDestiny: String,
    originIsUTC: Boolean = false,
    destinyIsUTC: Boolean = false
): String {
    val sdfOrigin = SimpleDateFormat(formatOrigin, Locale.getDefault())
    if (originIsUTC) sdfOrigin.timeZone = TimeZone.getTimeZone("UTC")

    val sdfDestiny = SimpleDateFormat(formatDestiny, Locale.getDefault())
    if (destinyIsUTC) sdfDestiny.timeZone = TimeZone.getTimeZone("UTC")

    return sdfDestiny.format(sdfOrigin.parse(strDate))
}


private fun getPictureIntent(context: Context, outputFileUri: Uri): Intent {
    val cameraIntents = ArrayList<Intent>()
    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val listCam = context.packageManager.queryIntentActivities(captureIntent, 0)
    for (res in listCam) {
        val intent = Intent(captureIntent)
        intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
        intent.setPackage(res.activityInfo.packageName)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        cameraIntents.add(intent)
    }

    val galleryIntent = Intent()
    galleryIntent.type = "image/*"
    galleryIntent.action = Intent.ACTION_PICK

    return Intent.createChooser(galleryIntent, context.getString(R.string.select_source))
        .putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray<Parcelable>())
}

fun FragmentActivity.showFragment(containerId: Int, fragment: Fragment, tag: String? = null) {
    supportFragmentManager.beginTransaction().replace(containerId, fragment, tag).commit()
}


fun getCurrency(value: Float): String =
    NumberFormat.getCurrencyInstance(Locale.getDefault()).format(value)

fun formatNumber(value: Double, digits: Int = 10): String {
    val numberFormat = NumberFormat.getInstance(Locale("es", "AR"))
    numberFormat.maximumFractionDigits = digits
    numberFormat.minimumFractionDigits = 2
    return numberFormat.format(value)
}

/*@SuppressLint("PackageManagerGetSignatures")
fun Context.printKeyHash() {
    try {
        val signatures =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
            else
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo.apkContentsSigners

        val md = MessageDigest.getInstance("SHA")
        for (signature in signatures) {
            md.update(signature.toByteArray())
            Log.i("MY_KEY_HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}*/

fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val mNotificationManager =
            getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager?
        val mChannel = NotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        mNotificationManager?.createNotificationChannel(mChannel)
    }
}

fun Context.launchShareText(text: String, package_: String? = null) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
        setPackage(package_)
    }

    if (package_ != null) {
        if (sendIntent.resolveActivity(packageManager) != null) startActivity(sendIntent)
    } else
        startActivity(Intent.createChooser(sendIntent, getString(R.string.app_name)))
}

fun Context.copyText(text: String) {
    val clipMan: ClipboardManager =
        getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipMan.setPrimaryClip(clip)
    Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
}