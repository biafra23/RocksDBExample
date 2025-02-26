package com.jaeckel.rocksdbexample

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.jaeckel.rocksdbexample.databinding.ActivityMainBinding
import org.rocksdb.CompressionType
import org.rocksdb.Options
import org.rocksdb.RocksDB
import org.rocksdb.RocksDBException
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        // Initialize RocksDB
        RocksDB.loadLibrary()

        val dbDir = File(filesDir, "rocksdb_test").absolutePath
        val options = Options().setCreateIfMissing(true)
//        options.setCompressionType(CompressionType.NO_COMPRESSION)
//        options.setCompressionType(CompressionType.ZLIB_COMPRESSION)
        options.setCompressionType(CompressionType.LZ4_COMPRESSION)
//        options.setCompressionType(CompressionType.LZ4HC_COMPRESSION)
        var db: RocksDB? = null

        try {
            db = RocksDB.open(options, dbDir)

            // Simple put and get example
            val key = "myKey".toByteArray()
            val value = "myValue".toByteArray()
            db.put(key, value)

            val retrievedValue = db.get(key)
            Log.d("RocksDBTest", "Retrieved value: ${String(retrievedValue)}")

        } catch (e: RocksDBException) {
            Log.e("RocksDBTest", "RocksDB error: ${e.message}")
            e.printStackTrace()
        } finally {
            db?.close()
            options.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}