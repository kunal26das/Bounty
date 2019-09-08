package kudos26.tabs.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import kotlinx.android.synthetic.main.actionbar.*
import kotlinx.android.synthetic.main.activity_main.*
import kudos26.tabs.R
import kudos26.tabs.core.Activity
import kudos26.tabs.utils.Constants.Companion.TOOLBAR_MARGIN_HORIZONTAL
import kudos26.tabs.utils.Constants.Companion.TOOLBAR_MARGIN_VERTICAL

class MainActivity : Activity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (toolbarWrapper?.layoutParams as CoordinatorLayout.LayoutParams).apply {
            setMargins(TOOLBAR_MARGIN_HORIZONTAL,
                    TOOLBAR_MARGIN_VERTICAL + getStatusBarHeight(),
                    TOOLBAR_MARGIN_HORIZONTAL,
                    TOOLBAR_MARGIN_VERTICAL)
        }
        setSupportActionBar(toolbar)
        initNavigation()
    }

    private fun initNavigation() {
        val navController = (supportFragmentManager.findFragmentById(R.id.app_navigation_fragment) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.app_navigation_fragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return try {
            menuInflater.inflate(R.menu.overflow_menu, menu)
            true
        } catch (e: Exception) {
            super.onCreateOptionsMenu(menu)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        return when (item.itemId) {
            else -> {
                super.onNavigationItemSelected(item)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!item.onNavDestinationSelected(findNavController(R.id.app_navigation_fragment))) {
            when (item.itemId) {
                R.id.dark_mode -> switchTheme(!isDarkTheme())
                //R.id.settings -> "Settings".snackBarForIndefiniteDuration()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.app_navigation_fragment).navigateUp(appBarConfiguration)
    }

}
