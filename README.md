# Ogya

Ogya is a set of tools for quick android development. It gives you a consistent way to display dialogs and load lists with only one recycler adapter. The adapter can handle multiple view types. Do more with less.

*   Quick Dialog for dialogs
*   Quick Lists for recycler views
*   Quick Permissions for permissions
 
## Usage
1. Enabled __android data binding__
    ```gradle
    android {
        ...
    
        dataBinding {
            enabled = true
        }
    }
    ```

2. __Add it in your root build.gradle at the end of repositories:__
    ```gradle
    	allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}
    ```
3. __Add the dependency__    
    ```gradle
    dependencies {
    	        implementation 'com.github.billkainkoom:ogya:0.75'
    	}
    ```



## Quick List (Listable Adapter)
![](https://github.com/billkainkoom/ogya/blob/master/images/listableadapter.jpg)

Quick list simply gives you one method to use for all types of list

## Idealistic way to use Quick List
First create an  Object eg(ListableTypes) in Kotlin like

```kotlin
object ListableTypes {
    val Person = ListableType(R.layout.person)
    val Animal = ListableType(R.layout.animal)
    val Furniture = ListableType(R.layout.furniture)
}
```

__Please note that all layouts used should be data-binding compatible, that is it must be of this form__
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout>
...
</layout>
```

Now let your classes that you wish to display in a list implement __Listable__
eg

```kotlin
data class MyPerson(val name: String = "", val email: String = "", val type: ListableType = ListableTypes.Person) : Listable {
    override fun getListableType(): ListableType? {

        return type
    }
}

data class Animal(val name: String = "", val specie: String = "") : Listable {
    override fun getListableType(): ListableType? {
        return ListableTypes.Animal
    }
}

data class Furniture(val name: String = "", val specie: String = "") : Listable {
    override fun getListableType(): ListableType? {
        return ListableType(R.layout.furniture)
    }
}
```


Well thats it you are almost there...

Assuming this was your data source

```kotlin
val peopleAndThings  = mutableListOf(
                MyPerson(name = "Kwasi Malopo", email = "kwasimalopo@outlook.com"),
                MyPerson(name = "Adwoa Lee", email = "adwoalee@gmail.com", type = ListableTypes.Furniture),
                Animal(name = "Cassava", specie = "Plantae"),
                Animal(name = "Cat", specie = "Felidae"),
                Furniture(name = "Cat", specie = "Felidae")
        )       
```

Go ahead and show your list by calling __loadList__ from __ListableHelper__

```kotlin
ListableHelper.loadList(
                context = context,
                recyclerView = recyclerView,
                listableType = ListableTypes.Person,
                listables = peopleAndThings,
                listableBindingListener = { listable, listableBinding, position ->
                    when (listable) {
                        is MyPerson -> {
                            if (listableBinding is PersonBinding) {
                                listableBinding.name.text = listable.name
                                listableBinding.email.text = listable.email
                            } else if (listableBinding is FurnitureBinding) {
                                listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp)
                                listableBinding.name.text = listable.name
                                listableBinding.specie.text = listable.email
                            }
                        }
                        is Animal -> {
                            if (listableBinding is AnimalBinding) {
                                listableBinding.name.text = listable.name
                                listableBinding.specie.text = listable.specie
                            }
                        }
                        is Furniture -> {
                            if (listableBinding is FurnitureBinding) {
                                listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp)
                                listableBinding.name.text = listable.name
                                listableBinding.specie.text = listable.specie
                            }
                        }
                    }

                },
                listableClickedListener = { listable, listableBinding, position ->
                    when (listable) {
                        is MyPerson -> {
                            Toast.makeText(context, listable.name, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                layoutManagerType = LayoutManager.Vertical
        )
```

__PersonBinding__ is the DataBinding Class that was generated by Android's Databinding Library for the layout __R.layout.person__
 

### Listable
Listable is an abstract class that all classes that you wish to display in a list should implement.

```kotlin
abstract class Listable(@Transient val identifier: String = "", @Transient val span: Int = 1) {
    abstract fun getListableType(): ListableType?
}
```

The ```identifier``` is used by the diffUtils to find out which objects are the same and those that have changed in the case of an update (When you submit a new List using ```listableAdapter.submitList(list)```).
The ```span``` is used when you want to have dynamic span lengths when using  ```LayoutManager.Grid``` to display your list. This helps you to create a list with items having different spans on different rows.

```@Transient``` : Marks the JVM backing field of the annotated property as transient, meaning that it is not part of the default serialized form of the object.
https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-transient/index.html

### ListableType
The listable type is a simple class that tells listable adapter what type of layout to use.
Its defined as

```kotlin
class ListableType(val layout: Int = 0)
```

### Listable Helper
The listableHelper is a set of functions and variables that make the usage of QuickList easier.
The constructor is internal to the module so it cannot be instantiated from your code. 
To display items in a list just call the public function __loadList(...)__ in __ListableHelper__

```kotlin
fun <T : Listable> loadList(context: Context, 
                                recyclerView: RecyclerView, 
                                listables: MutableList<T>, 
                                listableType: ListableType,
                                listableBindingListener: (T, ViewDataBinding, Int) -> Unit = { x, y, z -> },
                                listableClickedListener: (T, ViewDataBinding,Int) -> Unit = { x, y,z -> },
                                layoutManagerType: LayoutManager = LayoutManager.Vertical,
                                stackFromEnd: Boolean = false
    ): ListableAdapter<T>
```
 

 ### Arguments in LoadList  
 | Variable                | Purpose                                                                                                                                                               |
 |-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
 | context                 | context                                                                                                                                                               |
 | recyclerView            | is a reference to your recycler view                                                                                                                                  |
 | listables               | is a reference to your recycler viewis a mutable list of objects that you want to render in a list. All objects in this list should implement the Listable interface. |
 | listableType            | Default type to use when no listableType is not specified on an object                                                                                                |
 | listableBindingListener | An anonymous function that supplies you with a listable its position and a viewDataBinder to display information                                                      |
 | listableClickedListener | An anonymous function that supplies you with a listable its position and a viewDataBinder. It is called when an item on the list is clicked                           |
 | layoutManagerType       | Type of layout manager that ListableAdapter would set to the recyclerView supplied                                                                                    |
 | stackFromEnd            | A boolean which determines whether a recycler view should be stacked from end or not                                                                                  |


__loadList__ returns a __ListableAdapter<T>__ that you can use to add or remove elements from your list

### Methods on  ListableAdapter<T>

| Method                                   | Purpose                                                                            |
|------------------------------------------|------------------------------------------------------------------------------------|
| ```submitList(list: MutableList<T>)```         | Submits list to ListableAdapter -> Which would use diffutils to calculate changes and render results. This method is called in ListableHelper.loadList, so only use it when you want to update your list. It supersedes all other change (eg . Add , Remove , Replace etc ...) calls. |
| ```removeAt(position: Int)```                 | Removes listable at a position                                                     |
| ```addAt(position: Int, listable: T)```        | Add a listable at position                                                         |
| ```replaceAt(position: Int, listable: T)```    | Replace listable at position                                                       |
| ```addAt(position: Int, vararg listable: T)``` | Add listable(s) at position eg listableAdapter<T>.addAt(2,listable1,listable2,...) |
| ```addAt(position: Int, newListables: List)``` | Add listable(s) at position                                                        |
| ```add(newListables: List)```                  | Add listable(s). This would add to bottom of the list                              |
| ```add(listable: T)```                         | Add listable. This would add to bottom of the list                                 |


## Quick Dialog

![](https://github.com/billkainkoom/ogya/blob/master/images/quickdialogs.jpg)

Quick dialog simply gives you multiple consistent variants of dialogs you need in your  Android App.

  - Message Dialog
  - Progress Dialog
  - Alert Dialog
  - Input Dialog
  
### Message Dialog
 A message dialog simply displays an image with one button.

 ```kotlin
  QuickDialog(
                context = this,
                style = QuickDialogType.Message,
                title = "Hello World",
                message = "The quick dialog jumped over the old dialog",
                image = R.drawable.ic_info_outline_black_24dp)
                .overrideButtonNames("OK" ).overrideClicks({ ->
                    Toast.makeText(context, "Clicked on OK", Toast.LENGTH_SHORT).show()
                }).show()
 ```

 ### Progress Dialog
 A progress dialog shows a circular progress in an indeterminate state with or without a button

 ```kotlin
 QuickDialog(
                context = context,
                style = QuickDialogType.Progress,
                title = "Please wait",
                message = "Walking round the world")
                .show()
 ```

 This variant however shows a button so that a user can dismiss the dialog

 ```kotlin
QuickDialog(
                context = context,
                style = QuickDialogType.Progress,
                title = "Please wait",
                message = "Walking round the world")
                .overrideButtonNames("Hide Progress")
                .overrideClicks({ ->
                    Toast.makeText(context, "Clicked on Hide Progress", Toast.LENGTH_SHORT).show()
                }).showPositiveButton()
                .show()
 ```

### Alert Dialog
An alert dialog is used in situations when a user has to make a decision

```kotlin
QuickDialog(
                context = context,
                style = QuickDialogType.Alert,
                title = "Proceed",
                message = "Do you want to take this action")
                .overrideButtonNames("Yes", "No")
                .overrideClicks(positiveClick = { ->
                    Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show()
                }, negativeClick = { ->
                    Toast.makeText(context, "No", Toast.LENGTH_SHORT).show()
                })
                .show()
 ```

 The overrideClicks appears in three forms

 #### OverrideClicks #1
 ```kotlin
 fun overrideClicks(
            positiveClick: () -> Unit = {},
            negativeClick: () -> Unit = {},
            neutralClick: () -> Unit = {}
    )
 ```

 #### OverrideClicks #2
 ```kotlin
  fun overrideClicks(
            positiveClick: (dismiss: () -> Unit) -> Unit = { d -> },
            negativeClick: (dismiss: () -> Unit) -> Unit = { d -> },
            neutralClick: (dismiss: () -> Unit) -> Unit = { d -> }
    )
 ```
 The variable __d__ is an anonymos function that is passed from the implemetation
 of the overideClicks function. It is the __dismiss__ function in QuickDialog and it helps you dismiss the dialog in the click closure. All overloaded methods with __d__ supplied do not dismiss automatically.

 Lets see an example
 ```kotlin
 QuickDialog(
                context = context,
                style = QuickDialogType.Alert,
                title = "Proceed",
                message = "Do you want to take this action")
                .overrideButtonNames("Yes", "No")
                .overrideClicks(positiveClick = { dismiss ->
                    if (true) {
                        Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }, negativeClick = { dismiss ->
                    if (true) {
                        Toast.makeText(context, "No", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                })
                .show()
 ```

 If we dont invoke __dismiss__ the Quick dialog wont disappear.

 #### OverrideClicks #3
 ```kotlin
 fun overrideClicks(
            positiveClick: (dismiss: () -> Unit, inputText: String) -> Unit = { d, s -> },
            negativeClick: (dismiss: () -> Unit, inputText: String) -> Unit = { d, s -> },
            neutralClick: (dismiss: () -> Unit, inputText: String) -> Unit = { d, s -> }
    )
 ```

 The __d__ variable is same as the one described above. However the __s__ is text that a user entered in the __WithInput__ variation of the Quick dialog

 lets see an example
 ```kotlin
 QuickDialog(
                context = context,
                style = QuickDialogType.WithInput,
                title = "Verify Code",
                message = "Please verify the SMS code that was sent to you")
                .overrideButtonNames("Verify", "Cancel", "Re-send")
                .overrideClicks(positiveClick = { dismiss, inputText ->
                    if (inputText.length < 3) {
                        Toast.makeText(context, "Please enter a 4 digit code", Toast.LENGTH_SHORT).show()
                    } else if (inputText == "4000") {
                        Toast.makeText(context, "Verified", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(context, "You entered the wrong code", Toast.LENGTH_SHORT).show()
                    }
                }, negativeClick = { dismiss, inputText ->
                    dismiss()
                }, neutralClick = { dismiss, inputText ->
                    //Your action
                    dismiss()
                })
                .withInputHint("Code")
                .withInputLength(4)
                .withInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .showNeutralButton()
                .show()
 ```

## Quick Permissions
In pre-lollipop permission checking was not really an issue. Just add it to your manifest 
and you are good to go. However in the post lollipop era we have to deal with runtime permissions. Quick Permissions makes it easier for you to request permissions at runtime.

![](https://github.com/billkainkoom/ogya/blob/master/images/permissionhelper.jpg)

```kotlin
    val REQUEST_CODE = 100
    var permissionHelper: PermissionHelper? = null
    
    fun d8(context: Context) {
        permissionHelper = PermissionHelper(this, context)
        if (permissionHelper!!.requestPermissions(REQUEST_CODE, Manifest.permission.READ_CONTACTS)) {
            //permissions are granted , if not a call to ask for permission would be triggered
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    
            when (requestCode) {
                REQUEST_CODE -> {
                    val quickObject = QuickObject(0, "Calling is great", "MainActivity wants to read your contacts and send to Google", R.drawable.ic_info_outline_black_24dp, "")
                    permissionHelper!!.handlePermissionRequestResponse(quickObject, requestCode, permissions, grantResults, object : PermissionHelper.PermissionRequestListener {
                        override fun onPermissionRequestResponse(granted: Boolean) {
                            if (granted) {
                                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
    }
```

# Componentization

In previous versions of Ogya, the **listableBindingListener** looked something like this

```kotlin

listableBindingListener = { listable, listableBinding, position ->
		when (listable) {
			is MyPerson -> {
				if (listableBinding is PersonBinding) {
					listableBinding.name.text = listable.name
					listableBinding.email.text = listable.email
					
				} else  if (listableBinding is FurnitureBinding) {
					listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp)
					listableBinding.name.text = listable.name
					listableBinding.specie.text = listable.email
			}
		}
		is Animal -> {
			if (listableBinding is AnimalBinding) {
				listableBinding.name.text = listable.name
				listableBinding.specie.text = listable.specie
			}
		}
		is Furniture -> {
			if (listableBinding is FurnitureBinding) {
				listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp)
				listableBinding.name.text = listable.name
				listableBinding.specie.text = listable.specie
			}
		}
	}
}
```



This is *quite* ok. But Imagine you had a project where you had to display persons at multiple places. You would need to set the properties over and over again in all places. But with **Componentization**. Its done at one place and the change ripples across your entire project.


## **Componentization Axioms**

 - A component should have its dependencies injected into it
 - A components state is determined by the listable object that’s passed to it.
 
 

## **Origins**

All components are derived from the abstract class **BaseComponent.**

```kotlin
abstract class BaseComponent<V : ViewDataBinding, L : Listable> { 

	abstract fun render(binding: V, listable: L) 
	
	abstract fun listableType(): ListableType  
}
```


It’s a generic abstract class that expects a **ViewDataBinding** Type and a **Listable (any class that extends Listable)** Type. The **ViewDataBinding** files are generated classes created by any layout file which has a root parent of  ```<layout>```.


***Function: Render***

The render method is used to display the component.

***Function: ListableType***

The listableType function returns the listableType of the component.


## **Lets Dive In**

The **Kotlin** language provides as with beautiful structures. One such structure is **object**.

An **object** is a thread-safe singleton class. Our derived components are all **objects.** They have no constructors and they do not keep state. The only state they know of is the state of the **listable.**

```kotlin
object AnimalComponent : BaseComponent<AnimalBinding, Animal>() { 

	override fun render(binding: AnimalBinding, listable: Animal) { 
		binding.name.text = listable.name binding.specie.text = listable.specie 
	} 

	override fun listableType(): ListableType { 
		return ListableTypes.Animal 
	} 
}
```

**Then …**

```kotlin
ListableHelper.loadList( 
	context = context, 
	recyclerView = recyclerView, 
	listableType = ListableTypes.Person, 
	listables = people, 
	listableBindingListener = { listable, listableBinding, position -> 
		when (listable) { 
			is MyPerson -> { 
				MyPersonComponent.render(listableBinding as PersonBinding, listable) 
			} is Animal -> { 
				AnimalComponent.render(listableBinding as AnimalBinding, listable) 
			} is Furniture -> { 
				//old way 
				if (listableBinding is FurnitureBinding) { 						     
					listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp) 
					listableBinding.name.text = listable.name listableBinding.specie.text = listable.specie 
				} 
			}
		}
	},
	listableClickedListener = { listable, listableBinding, position -> 
			when (listable) { 
				is MyPerson -> { 
					Toast.makeText(context, listable.name, Toast.LENGTH_SHORT).show() 
					} 
				} 
			}, 
	layoutManagerType = LayoutManager.Vertical 
)
```


This makes it easy to reuse code. As long as dependencies are provided for render, the components can be recreated with its expected behavior.


# Ogya Forms
There are hardly any android apps built without forms. It can be daunting especially when you have forms all over the place. 
Usually they lead to never ending xml files or really long xml files. And then there is the case where the form contains other elements
which are not inputs eg info cards, images etc. Most at times we will just repeat the form code in all xmls that they are needed in
or probably use the include tag (but there is no for-loop for <include> (not that I know of though.)). We have already achieved 
component reuse by Componentization in Ogya. All we needed to do was to 

### 1. Add a component that can accept input.
```kotlin
enum class QuickFormInputType {
     Input,
     Date,
     Time
 }
 
data class QuickFormInputElement(
         val name: String = "",
         var value: String = "",
         val placeholder: String = "",
         val hint: String = "",
         val inputLength : Int = 1000,
         val inputType : Int = InputType.TYPE_CLASS_TEXT,
         val type: QuickFormInputType = QuickFormInputType.Input,
         val ofListableType: ListableType = OgyaListableTypes.QuickFormInput
 ) : Listable() {
 
     override fun getListableType(): ListableType? {
         return ofListableType
     }
 }
 ```

I see the confusion in **type** and **inputType**. So lets break that down.

**type** 
This just toggles between date,time and input. Basically they are all inputs but **time** gives you the 
opportunity to select from the android TimePicker, **date** also does the same but gives you a DatePicker instead.
Both type **time** and **date** gives you an un-editable input. Type input allows you to use the keyboard as the input source.




### 2. Decide the keyboard type based on the input.
```kotlin
private fun handleInputType(binding: ComponentQuickFormInputBinding,listable: QuickFormInputElement) {
        binding.input.enableEditing(true)
        binding.input.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(listable.inputLength))
        toggleButtonVisibility(binding, false)

        try {
            binding.input.inputType = listable.inputType
        } catch (e: Exception) {
            Log.e("QuickForm", "Invalid input type")
            e.printStackTrace()
        }
    }
```

As you can see the **inputType** parameter expects the android textInput type. Pass any of these and you will get its respective
keyboard. 

```kotlin
object ComponentQuickFormInput : BaseComponent<ComponentQuickFormInputBinding, QuickFormInputElement>() {

    override fun render(binding: ComponentQuickFormInputBinding, listable: QuickFormInputElement) {
        binding.input.setText(listable.value)
        binding.inputLayout.hint = listable.hint

        binding.input.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.input.hint = listable.placeholder
            } else {
                binding.input.hint = ""
            }
        }



        when (listable.type) {
            QuickFormInputType.Input -> {
                handleInputType(binding,listable)
            }
            QuickFormInputType.Date -> {
                handleDateType(binding)
            }
            QuickFormInputType.Time -> {
                handleTimeType(binding)
            }
        }
    }
}
```

### 3. Add a watcher to track value changes of input element.
```kotlin
  inner class ListableViewHolder(val viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root) {

        init {
            viewBinding.root.setOnClickListener { listableClickedListener(getItem(adapterPosition), viewBinding, adapterPosition) }

            //form functionality
            (viewBinding.root.findViewWithTag<View>("input") as? EditText)?.let { input->
                input.watch { text ->
                    if(getItem(adapterPosition) is QuickFormInputElement){
                        (getItem(adapterPosition) as QuickFormInputElement).value = text
                    }
                }
            }
        }
    }
```

This means you can pass any layout you prefer. Just make sure of the following. 

1. The layout has an **EditText** with **tag** "input". If absent the watcher wont recognize any changes since its only watching
for changes on an EditText with tag **input**.

2. Your component's signature is
```kotlin
object Component : BaseComponent<ComponentBinding, QuickFormInputElement>() {
    
}
```


ListableAdapter has been extended with one more function : **retrieveFormValues**
```kotlin
fun retrieveFormValues() : HashMap<String,String>{
        val formData = hashMapOf<String,String>()
        for(listable in listables){
            if(listable is QuickFormInputElement){
                formData[listable.name] = listable.value
            }
        }
        return formData
}
```

And you use it like this: 
```kotlin
fun loadList(context: Context,binding:ActivityFormBinding, recyclerView: RecyclerView): ListableAdapter<Listable> {
        val form = mutableListOf(
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
                        placeholder = "Kasoa 212 LP",
                        inputType = InputType.TYPE_CLASS_NUMBER
                ),
                QuickFormInputElement(
                        name = "phone_number",
                        value = "",
                        hint = "Phone Number",
                        placeholder = "0266 175 924",
                        inputType = InputType.TYPE_CLASS_PHONE
                ),
                MyPerson(name = "Adwoa Lee", email = "adwoalee@gmail.com"),
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
                )
        )


        var adapter : ListableAdapter<Listable>? = null
        adapter = ListableHelper.loadList(
                context = context,
                recyclerView = recyclerView,
                listableType = ListableTypes.Person,
                listables = form,
                listableBindingListener = { listable, listableBinding, position ->
                    when (listable.getListableType()) {
                        OgyaListableTypes.QuickFormInput -> {
                            ComponentQuickFormInput.render(listableBinding as ComponentQuickFormInputBinding, listable as QuickFormInputElement)
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

        binding.submit.setOnClickListener {
            var results : HashMap<String,String> = adapter.retrieveFormValues()
        }

        return adapter
    }
```

And that's it! 

![](https://github.com/billkainkoom/ogya/blob/master/images/time_picker.png)

![](https://github.com/billkainkoom/ogya/blob/master/images/date_picker.png)

![](https://github.com/billkainkoom/ogya/blob/master/images/form.png)

![](https://github.com/billkainkoom/ogya/blob/master/images/form_results.png)
   
```groovy
	dependencies { implementation 'com.github.billkainkoom:ogya:0.75' }
```