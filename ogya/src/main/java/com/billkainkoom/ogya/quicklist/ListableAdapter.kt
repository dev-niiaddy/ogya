package com.billkainkoom.ogya.quicklist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.billkainkoom.ogya.extentions.watch
import com.billkainkoom.ogya.shared.QuickFormInputElement

/**
 * Created by  Bill Kwaku Ansah Inkoom on 6/22/2015.
 */

data class InputValue(var tag: String, var value: String)

class ListableAdapter<T : Listable> internal constructor(
        private val context: Context,
        private val listableType: ListableType,
        var listables: MutableList<T>,
        private val listableBindingListener: (T, ViewDataBinding, Int) -> Unit,
        private val listableClickedListener: (T, ViewDataBinding, Int) -> Unit,
        private val inputTags: List<String>,
        private val inputChangeListener: (T, Int, InputValue) -> Unit,
        var isRecyclable: Boolean = true
) : ListAdapter<T, ListableAdapter<T>.ListableViewHolder>(ListableAdapterDiffCallback<T>()) {

    private var currentListableType: ListableType? = null

    class ListableAdapterDiffCallback<T : Listable> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.identifier == newItem.identifier
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position).getListableType() == null) {
            currentListableType = listableType
            return listableType.layout
        }
        currentListableType = getItem(position).getListableType()
        return getItem(position).getListableType()!!.layout

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListableViewHolder {
        return ListableViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), currentListableType!!.layout, viewGroup, false))
    }

    override fun onBindViewHolder(listableViewHolder: ListableViewHolder, listablePosition: Int) {
        listableViewHolder.setIsRecyclable(isRecyclable)
        listableBindingListener(getItem(listablePosition), listableViewHolder.viewBinding, listablePosition)
    }

    inner class ListableViewHolder(val viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root) {

        init {
            viewBinding.root.setOnClickListener { listableClickedListener(getItem(adapterPosition), viewBinding, adapterPosition) }

            //form functionality
            (viewBinding.root.findViewWithTag<View>("input") as? EditText)?.let { input ->
                input.watch(textChanged = { text ->
                    if (getItem(adapterPosition) is QuickFormInputElement) {
                        (getItem(adapterPosition) as QuickFormInputElement).value = text
                    }
                }, afterTextChanged = { text ->
                    if (getItem(adapterPosition) is QuickFormInputElement) {
                        (getItem(adapterPosition) as? QuickFormInputElement)?.let { quickFormInputElement ->
                            quickFormInputElement.textWatcher?.let { textWatcher ->
                                textWatcher.afterTextChanged(text)
                                input.setSelection(input.text.length)
                            }
                        }
                    }
                })
            }

            //custom form functionality
            for (inputTag in inputTags) {
                (viewBinding.root.findViewWithTag<View>(inputTag) as? EditText)?.let { input ->
                    input.watch(textChanged = { text ->
                        inputChangeListener(getItem(adapterPosition), adapterPosition, InputValue(tag = inputTag, value = text))
                    }, afterTextChanged = { text ->
                        input.setSelection(input.text.length)
                    })
                }
            }
        }
    }

    fun retrieveFormValues(): HashMap<String, String> {
        val formData = hashMapOf<String, String>()
        for (listable in listables) {
            if (listable is QuickFormInputElement) {
                formData[listable.name] = listable.value
            }
        }
        return formData
    }

    fun removeAt(position: Int) {
        listables.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listables.size)
    }

    fun clear() {
        val size = listables.size
        listables.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addAt(position: Int, listable: T) {
        listables.add(position, listable)
        notifyItemInserted(position)
    }

    fun replaceAt(position: Int, listable: T) {
        listables[position] = listable
        notifyItemChanged(position)
    }

    fun addAt(position: Int, vararg listable: T) {
        listables.addAll(position, listable.asList())
        notifyItemInserted(listables.size - 1)
        notifyItemRangeChanged(position, listables.size)
    }

    fun addAt(position: Int, newListables: List<T>) {
        listables.addAll(position, newListables)
        notifyItemInserted(listables.size - 1)
        notifyItemRangeChanged(position, listables.size)
    }

    fun add(newListables: List<T>) {
        (newListables as ArrayList).removeAll(listables)
        val preSize = listables.size
        listables.addAll(newListables)
        notifyItemInserted(listables.size - 1)
        notifyItemRangeChanged(preSize, listables.size)
    }

    fun add(listable: T) {
        listables.add(listable)
        notifyItemInserted(listables.size - 1)
    }
}