package com.kelvins.launcher

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var root: LinearLayout
    private lateinit var appArea: GridLayout
    private var currentMode = "ORBIT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLauncher()
    }

    private fun showLauncher() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 30, 20, 20)
            setBackgroundColor(Color.rgb(12, 16, 35))
        }

        val title = TextView(this).apply {
            text = "KELVINS"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "YOUR PHONE. YOUR UNIVERSE."
            textSize = 12f
            setTextColor(Color.CYAN)
            gravity = Gravity.CENTER
            setPadding(0, 5, 0, 20)
        }

        root.addView(title)
        root.addView(subtitle)

        val modes = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        listOf("ORBIT", "CAROUSEL", "CUBE").forEach { mode ->
            val button = Button(this).apply {
                text = mode
                setOnClickListener {
                    currentMode = mode
                    renderApps()
                }
            }

            modes.addView(
                button,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )
        }

        root.addView(modes)

        val scroll = ScrollView(this)

        appArea = GridLayout(this).apply {
            columnCount = 3
            setPadding(0, 20, 0, 20)
        }

        scroll.addView(appArea)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
        renderApps()
    }

    private fun renderApps() {
        appArea.removeAllViews()

        appArea.columnCount = when (currentMode) {
            "CAROUSEL" -> 1
            "CUBE" -> 2
            else -> 3
        }

        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = packageManager
            .queryIntentActivities(intent, 0)
            .sortedBy { it.loadLabel(packageManager).toString() }

        for (app in apps) {
            val name = app.loadLabel(packageManager).toString()
            val icon = app.loadIcon(packageManager)

            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(8, 16, 8, 16)
            }

            val iconView = android.widget.ImageView(this).apply {
                setImageDrawable(icon)
                layoutParams = LinearLayout.LayoutParams(72, 72)
            }

            val label = TextView(this).apply {
                text = name
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                setPadding(0, 8, 0, 0)
            }

            item.addView(iconView)
            item.addView(label)

            item.setOnClickListener {
                val launchIntent = packageManager
                    .getLaunchIntentForPackage(app.activityInfo.packageName)

                if (launchIntent != null) {
                    startActivity(launchIntent)
                } else {
                    Toast.makeText(
                        this,
                        "Cannot open $name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            appArea.addView(
                item,
                GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                    )
                }
            )
        }
    }
}
