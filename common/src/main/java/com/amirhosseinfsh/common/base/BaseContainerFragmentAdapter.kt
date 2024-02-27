package com.amirhosseinfsh.common.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amirhosseinfsh.core.util.MAIN_EXISTING_FRAGMENTS

open class BaseContainerFragmentAdapter(
    fa: FragmentActivity,
    protected val fragments: ArrayList<Fragment>
) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = fragments.size

    constructor(fa: FragmentActivity, list: List<Fragment>) : this(fa, ArrayList(list))

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}

class LazyBottomNavFragmentAdapter(
    fa: FragmentActivity,
    private val frmList: ArrayList<Fragment>,
    private val sameType: Boolean = false
) : BaseContainerFragmentAdapter(fa, frmList) {

    init {
        fa.lifecycle.addObserver(object: LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    fa.intent.putExtra(MAIN_EXISTING_FRAGMENTS, ArrayList<String>(getExistingFragmentNames()))
                }
            }
        })
    }
    constructor(fa: FragmentActivity, initialFragment: Fragment,sameType: Boolean = false): this(fa, arrayListOf(initialFragment),sameType)

    fun<T: Fragment> getFrmList() = frmList.map { it as? T }

    fun getExistingFragmentNames() =
        fragments.mapIndexed { index, frm ->
            val name = if (sameType)
                if (frm.tag.orEmpty() != "f$index") frm.tag.orEmpty().plus(index) else frm.tag.orEmpty()
            else frm::class.java.name
            name
        }

    fun addOrReturnPosFragment(frm: Fragment): Int {
        return getExistingFragmentNames()
            .let { itKeys ->
                itKeys.contains(frm::class.java.name)
                    .let { isExist ->
                        if (isExist) {
                            itKeys.indexOf(frm::class.java.name)
                        } else {
                            fragments.add(frm)
                            fragments.size - 1
                        }
                    }
            }
    }

    fun addOrReturnPosFragment(frm: Fragment,pageCounter: Int): Int {
        val frmName = frm::class.java.name.plus(pageCounter)
        return getExistingFragmentNames()
            .let { itKeys ->
                itKeys.contains(frmName)
                    .let { isExist ->
                        if (isExist) {
                            itKeys.indexOf(frmName)
                        } else {
                            fragments.add(frm)
                            fragments.size - 1
                        }
                    }
            }
    }

    fun returnFrmPosByTag(tag: String): Int? {
        return getExistingFragmentNames().indexOf(tag).takeIf { it != -1 }
    }

}

