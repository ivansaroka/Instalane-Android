package com.infinix.instalane.ui.start.tutorial

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.infinix.instalane.R
import com.infinix.instalane.data.local.TutorialEntity
import com.infinix.instalane.databinding.ActivityTutorialBinding
import com.infinix.instalane.ui.base.ActivityAppBase


class TutorialActivity : ActivityAppBase() {

    private val binding by lazy { ActivityTutorialBinding.inflate(layoutInflater) }

    var currentPage = 0
    val list = ArrayList<TutorialEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpTutorial()

        binding.mPrevious.setOnClickListener {
            currentPage--
            if (currentPage < 0)
                currentPage = 0
            binding.mViewPager.setCurrentItem(currentPage, true)
        }

        binding.mNext.setOnClickListener {
            currentPage++
            if (currentPage > list.size)
                currentPage = list.size - 1
            binding.mViewPager.setCurrentItem(currentPage, true)
        }

        binding.mViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun setUpTutorial(){
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut1 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut2 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut3 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut4 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut5 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut6 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut7 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut8 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut9 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut10 })
        list.add(TutorialEntity().apply { this.drawableID = R.drawable.tut11 })

        val adapter = TutorialAdapter(list)
        adapter.onCloseTutorial = { finish() }
        binding.mViewPager.adapter = adapter
    }

}