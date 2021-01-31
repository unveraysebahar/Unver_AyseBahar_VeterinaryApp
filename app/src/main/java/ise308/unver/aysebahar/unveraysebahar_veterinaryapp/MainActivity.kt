package ise308.unver.aysebahar.unveraysebahar_veterinaryapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    var animalList = ArrayList<Animal>()
    lateinit var adapter: RecyclerView.Adapter<*>
    lateinit var recyclerView: RecyclerView

    companion object{
        lateinit var database : DataManager
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_add_animal,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_animal_item){
            val intent = Intent(this, AddAnimalActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        database = DataManager(this, null, null, 1)

        viewAnimal()
    }

    @SuppressLint("WrongConstant")
    private fun viewAnimal() {
        animalList = database.getAnimal(this)
        adapter = AnimalAdapter(this,animalList)

        recyclerView = findViewById(R.id.recView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        viewAnimal()
        super.onResume()
    }
}