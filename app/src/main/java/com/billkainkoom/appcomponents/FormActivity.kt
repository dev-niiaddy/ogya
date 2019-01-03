package com.billkainkoom.appcomponents

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.billkainkoom.appcomponents.databinding.AnimalBinding
import com.billkainkoom.appcomponents.databinding.FurnitureBinding
import com.billkainkoom.appcomponents.databinding.PersonBinding
import com.billkainkoom.ogya.databinding.ComponentQuickFormInputBinding
import com.billkainkoom.ogya.quicklist.LayoutManager
import com.billkainkoom.ogya.quicklist.Listable
import com.billkainkoom.ogya.quicklist.ListableAdapter
import com.billkainkoom.ogya.quicklist.ListableHelper
import com.billkainkoom.ogya.quicklist.formcomponents.ComponentQuickFormInput
import com.billkainkoom.ogya.shared.OgyaListableTypes
import com.billkainkoom.ogya.shared.QuickFormInputElement
import com.billkainkoom.ogya.shared.QuickFormInputType

class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val cardObjects: RecyclerView = findViewById(R.id.card_objects)
        loadList(this, cardObjects)
    }

    fun loadList(context: Context, recyclerView: RecyclerView): ListableAdapter<Listable> {
        val people = mutableListOf(
                QuickFormInputElement(
                        name = "time",
                        value = "",
                        hint = "Time",
                        placeholder = "17:00",
                        type = QuickFormInputType.Time
                ),
                QuickFormInputElement(
                        name = "firstname",
                        value = "",
                        hint = "Firstname",
                        placeholder = "Kwame"
                ),
                QuickFormInputElement(
                        name = "lastname",
                        value = "",
                        hint = "Lastname",
                        placeholder = "Lee"
                ),
                QuickFormInputElement(
                        name = "address",
                        value = "",
                        hint = "Address",
                        placeholder = "Kasoa 212 LP"
                ),
                QuickFormInputElement(
                        name = "phone_number",
                        value = "",
                        hint = "Phone Number",
                        placeholder = "0266 175 924"
                ),
                QuickFormInputElement(
                        name = "date",
                        value = "",
                        hint = "Date",
                        placeholder = "22-01-1992",
                        type = QuickFormInputType.Date
                ),
                QuickFormInputElement(
                        name = "time",
                        value = "",
                        hint = "Time",
                        placeholder = "17:00",
                        type = QuickFormInputType.Time
                ),
                MyPerson(name = "Adwoa Lee", email = "adwoalee@gmail.com"),
                QuickFormInputElement(
                        name = "firstname",
                        value = "",
                        hint = "Firstname",
                        placeholder = "Kwame"
                ),
                QuickFormInputElement(
                        name = "lastname",
                        value = "",
                        hint = "Lastname",
                        placeholder = "Lee"
                ),
                QuickFormInputElement(
                        name = "address",
                        value = "",
                        hint = "Address",
                        placeholder = "Kasoa 212 LP"
                ),
                Animal(name = "Cassava", specie = "Plantae"),
                QuickFormInputElement(
                        name = "phone_number",
                        value = "",
                        hint = "Phone Number",
                        placeholder = "0266 175 924"
                ),
                QuickFormInputElement(
                        name = "date",
                        value = "",
                        hint = "Date",
                        placeholder = "22-01-1992",
                        type = QuickFormInputType.Date
                ),
                Furniture(name = "Cat", specie = "Felidae"),
                QuickFormInputElement(
                        name = "time",
                        value = "",
                        hint = "Time",
                        placeholder = "17:00",
                        type = QuickFormInputType.Time
                )
        )


        var a : ListableAdapter<Listable>? = null
        a = ListableHelper.loadList(
                context = context,
                recyclerView = recyclerView,
                listableType = ListableTypes.Person,
                listables = people,
                listableBindingListener = { listable, listableBinding, position ->
                    when (listable.getListableType()) {
                        OgyaListableTypes.QuickFormInput -> {
                            ComponentQuickFormInput.render(listableBinding as ComponentQuickFormInputBinding, listable as QuickFormInputElement,a,position)
                        }
                        ListableTypes.Person -> {
                            MyPersonComponent.render(listableBinding as PersonBinding, listable as MyPerson)
                        }
                        ListableTypes.Animal -> {
                            AnimalComponent.render(listableBinding as AnimalBinding, listable as Animal)
                        }
                        ListableTypes.Furniture -> {
                            FurnitureComponent.render(listableBinding as FurnitureBinding, listable as Furniture)
                        }
                    }

                },
                listableClickedListener = { listable, listableBinding, position ->

                },
                layoutManagerType = LayoutManager.Vertical
        )

        return a
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}