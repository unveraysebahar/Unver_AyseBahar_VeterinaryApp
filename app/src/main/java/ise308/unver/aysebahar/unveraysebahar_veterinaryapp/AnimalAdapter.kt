package ise308.unver.aysebahar.unveraysebahar_veterinaryapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add_animal.view.*
import kotlinx.android.synthetic.main.listitem.view.*
import java.lang.Exception

class AnimalAdapter(context: Context, val animals: ArrayList<Animal>) :
        RecyclerView.Adapter<AnimalAdapter.ViewHolder>(){

    val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.listitem,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal : Animal = animals[position]
        holder.animalName.text = animal.animalName
        holder.animalType.text = animal.animalType
        holder.animalAge.text = animal.animalAge.toString()
        holder.animalOwnerNumber.text = animal.animalOwnerNumber

        // IF NO PICTURE PUT THE 'VET' PICTURE

        if(animal.image == "null"){
            holder.animalImage.setImageResource(R.drawable.vet)
        }
        else{
            holder.animalImage.setImageURI(Uri.parse(animal.image))
        }

        // CHANGING THE PICTURE OF THE CARD VIEW ACCORDING TO THE BOOLEAN VARIABLE

        if(animal.vaccine == true){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#e6ecfa"))
        }
        else{
            holder.cardView.setCardBackgroundColor(Color.parseColor("#f9b7af"))
        }

        holder.btnDelete.setOnClickListener{
            val animalName = animal.animalName
            AlertDialog.Builder(context)
                .setTitle("DELETE ANIMAL")
                .setMessage("ARE YOU SURE YOU WANT TO DELETE ANIMAL?")
                .setPositiveButton("YES", DialogInterface.OnClickListener{dialog, which ->
                    try{
                        if(MainActivity.database.deleteAnimal(animal.animalID)){
                            animals.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position,animals.size)
                            Toast.makeText(context, "ANIMAL $animalName DELETING SUCCESSFUL", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: Exception){
                        print(e)
                    }
                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->  })
                .show()
        }

        holder.btnEdit.setOnClickListener{
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.dialog_edit_animal, null)
            val editAnimalNameText : TextView = view.findViewById(R.id.editAnimalNameText)
            val editAnimalTypeText : TextView = view.findViewById(R.id.editAnimalTypeText)
            val editAnimalAgeText : TextView = view.findViewById(R.id.editAnimalAgeText)
            val editAnimalOwnerNumberText : TextView = view.findViewById(R.id.editAnimalOwnerNumberText)
            val vaccineCheckBox : CheckBox = view.findViewById(R.id.vaccineCheckBox)

            editAnimalNameText.text = animal.animalName
            editAnimalTypeText.text = animal.animalType
            editAnimalAgeText.text = animal.animalAge.toString()
            editAnimalOwnerNumberText.text = animal.animalOwnerNumber
            vaccineCheckBox.isChecked = animal.vaccine!!

            val builder = AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton("SAVE", DialogInterface.OnClickListener{dialog, which ->
                    val isEditMode : Boolean = MainActivity.database.editAnimal(
                        animal.animalID.toString(),
                        view.editAnimalNameText.text.toString(),
                        view.editAnimalTypeText.text.toString(),
                        view.editAnimalAgeText.text.toString(),
                        view.editAnimalOwnerNumberText.text.toString(),
                        view.vaccineCheckBox.text.toString())

                    try{
                        if(isEditMode == true){
                            animals[position].animalName = view.editAnimalNameText.text.toString()
                            animals[position].animalType = view.editAnimalTypeText.text.toString()
                            animals[position].animalAge = view.editAnimalAgeText.text.toString().toInt()
                            animals[position].animalOwnerNumber = view.editAnimalOwnerNumberText.text.toString()
                            animals[position].vaccine = view.vaccineCheckBox.isChecked

                            notifyDataSetChanged()
                            Toast.makeText(context,"EDIT SUCCESSFUL",Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:Exception){
                        print(e)
                    }
                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->

                })
            val alert= builder.create()
            alert.show()
        }
    }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val animalName = itemView.animalName
            val animalType = itemView.animalType
            val animalAge = itemView.animalAge
            val animalOwnerNumber = itemView.animalOwnerNumber
            val cardView = itemView.cardView
            val btnDelete = itemView.btnDelete
            val btnEdit = itemView.btnEdit
            val animalImage = itemView.imageView
        }

        override fun getItemCount(): Int {
            return animals.size
        }
    }