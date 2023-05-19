package com.lksprovinsi.golibrary.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.resources.TextAppearance
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.lksprovinsi.golibrary.R
import com.lksprovinsi.golibrary.databinding.ActivityMainBinding
import com.lksprovinsi.golibrary.databinding.WidgetBottomNavItemBinding
import com.lksprovinsi.golibrary.libraries.StorageBox
import com.lksprovinsi.golibrary.libraries.adapters.MainViewPagerAdapter
import com.lksprovinsi.golibrary.views.fragments.CartFragment
import com.lksprovinsi.golibrary.views.fragments.ForumFragment
import com.lksprovinsi.golibrary.views.fragments.HomeFragment
import com.lksprovinsi.golibrary.views.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.bottomTl.addOnTabSelectedListener(object: OnTabSelectedListener{
            override fun onTabSelected(tab: Tab?) {
                setActiveFragment()
            }

            override fun onTabUnselected(tab: Tab?) {
                setActiveFragment()
            }

            override fun onTabReselected(tab: Tab?) { }
        })

        setBottomTabItems()
    }

    override fun onStart() {
        super.onStart()

        StorageBox.initGlobal(this)

        if(StorageBox.global!!.get("token", String::class.java) == null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val email: String? = StorageBox.global!!.get("email", String::class.java)
        if(email != null) StorageBox.initUser(email, this)
    }

    private fun setBottomTabItems(){
        val items: List<String> = listOf("Home", "Forum", "My Cart", "My Profile")

        for(item in items){
            val tab: Tab = binding.bottomTl.newTab()
            tab.text = item
            binding.bottomTl.addTab(tab)
        }

        binding.bottomTl.selectTab(binding.bottomTl.getTabAt(0))
    }

    private fun setActiveFragment(){

        val fragment: Fragment = when (binding.bottomTl.selectedTabPosition){
            0 -> HomeFragment()
            1 -> ForumFragment()
            2 -> CartFragment()
            3 -> ProfileFragment()
            else -> HomeFragment()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, fragment)
            .commit()
    }
}