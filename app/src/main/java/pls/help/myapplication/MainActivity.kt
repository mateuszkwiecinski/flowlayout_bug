package pls.help.myapplication

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.nex3z.flowlayout.FlowLayout
import com.thedeanda.lorem.Lorem
import com.thedeanda.lorem.LoremIpsum
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recycler = requireViewById<RecyclerView>(R.id.list)
        val lorem: Lorem = LoremIpsum.getInstance()
        recycler.adapter = SimpleAdapter().apply {
            val randomList = List(500) {
                List((6..30).random()) {
                    if (Math.random() > 0.5) {
                        lorem.lastName
                    } else {
                        """${lorem.name}(${lorem.city})"""
                    }
                }
            }
            submitList(randomList)
        }
        recycler.addItemDecoration(
            VerticalSpaceDecoration(
                verticalSpaceHeight = resources.getDimension(R.dimen.space).roundToInt()
            )
        )
    }
}


class SimpleAdapter : ListAdapter<DataModel, SimpleViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return SimpleViewHolder(inflater.inflate(R.layout.item_view, parent, false))
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

typealias DataModel = List<String>

object Diff : DiffUtil.ItemCallback<DataModel>() {
    override fun areItemsTheSame(oldItem: DataModel, newItem: DataModel) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: DataModel, newItem: DataModel): Boolean =
        oldItem == newItem
}

data class SimpleViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private val inflater = LayoutInflater.from(view.context)
    private val flow = view.requireViewById<FlowLayout>(R.id.flowLayout)

    fun bind(items: List<String>) {
        val reusableChildren = flow.children.toList()

        // Remove views we don't want to reuse
        (reusableChildren.size - items.size)
            .takeIf { it > 0 }
            ?.let { diff ->
                reusableChildren.takeLast(diff)
                    .forEach { flow.removeView(it) }
            }

        items.forEachIndexed { idx, label ->
            val chip = reusableChildren.getOrElse(
                index = idx,
                defaultValue = {
                    val newItem = inflater.inflate(R.layout.element_view, flow, false)
                    flow.addView(newItem)
                    newItem
                }
            )

            val title = chip.requireViewById<TextView>(R.id.title)
            title.text = label
        }
    }
}

class VerticalSpaceDecoration(private val verticalSpaceHeight: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        outRect.bottom = verticalSpaceHeight
    }
}
