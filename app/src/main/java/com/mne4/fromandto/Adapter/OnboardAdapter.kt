package com.mne4.fromandto.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mne4.fromandto.Fragment.OnboardFragment
import com.mne4.fromandto.R

class OnboardAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {
    private val titles = arrayOf("Добро пожаловать!", "Удобный поиск", "Любой маршрут на ваш вкус")
    private val descriptions = arrayOf("Сервис совместных поездок",
        "Находите попутчиков или водителей через поисковик",
        "Создавайте запрос, если результаты поиска не удовлетворяет")
    private val images = arrayOf(R.drawable.baseline_directions_car_24, R.drawable.baseline_search_24, R.drawable.baseline_map_24)
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardFragment.newInstance(
            titles[position],
            descriptions[position],
            position,
            images[position]
        )
    }

}