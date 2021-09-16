package com.crocodic.core.widget.stateview

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.ArrayMap
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class StateView : FrameLayout{

    private var mState: String? = null
    private var mDirtyFlag = false
    private var mInitialized = false
    private val mStateViews: ArrayMap<String, View?> = ArrayMap()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        if(!mInitialized) onSetupContentState()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle().apply {
            putParcelable(SAVED_INSTANCE_STATE, super.onSaveInstanceState())
        }
        saveInstanceState(bundle)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var bundle = state
        if (state is Bundle) {
            if (mState == null) restoreInstanceState(state)
            bundle = state.getParcelable<Bundle>(SAVED_INSTANCE_STATE)
        }
        super.onRestoreInstanceState(bundle)
    }

    fun saveInstanceState(outState: Bundle) {
        if(mState != null) outState.putString(SAVED_STATE, mState)
    }

    fun restoreInstanceState(savedInstanceState: Bundle) : String? {
        val state = savedInstanceState.getString(SAVED_STATE)
        state?.let { setState(it) }
        return state
    }

    protected fun onSetupContentState() {
        if(childCount != 1 + mStateViews.size) {
            throw IllegalStateException("Invalid child count. StateView must have exactly one child.")
        }
        val contentView = getChildAt(mStateViews.size)
        removeView(contentView)
        setStateView(CONTENT, contentView)
        setState(CONTENT)
        mInitialized = true
    }

    fun setStateView(state: String, view: View) {
        if(mStateViews.containsKey(state)) {
            removeView(mStateViews[state])
        }
        mStateViews[state] = view
        if(view.parent == null) {
            addView(view)
        }
        view.visibility = GONE
        mDirtyFlag = true
    }

    fun setState(state: String, overContent: Boolean = true) {
        if(getStateView(state) == null) {
            throw IllegalStateException(String.format("Cannot switch to state \"%s\". This state was not defined or the view for this state is null.", state))
        }

        if(mState != null && mState.equals(state) && !mDirtyFlag) return

        mState = state

        for (s in mStateViews.keys) {
            mStateViews[s]?.visibility = if (s == state) VISIBLE else GONE
        }

        if (overContent) {
            mStateViews[CONTENT]?.visibility = VISIBLE
        }

        mDirtyFlag = false
    }

    fun getStateView(state: String): View? {
        return mStateViews[state]
    }

    companion object {
        const val SAVED_INSTANCE_STATE = "instanceState"
        const val SAVED_STATE = "stateful_layout_state"
        const val CONTENT = "content"
    }
}