package com.crocodic.core.widget.currency

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class MoneyEditText : AppCompatEditText {

    companion object {
        private val MAX_LENGTH = 11
        private val MAX_DECIMAL = 3
        private val locale = Locale.US
    }

    private var prefix = ""
    private val currencyTextWatcher = CurrencyTextWatcher(this) //prefix

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        this.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        //this.setHint(prefix);
        this.filters = arrayOf(InputFilter.LengthFilter(MAX_LENGTH))
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            this.addTextChangedListener(currencyTextWatcher)
        } else {
            this.removeTextChangedListener(currencyTextWatcher)
        }
        handleCaseCurrencyEmpty(focused)
    }

    /**
     * When currency empty <br/>
     * + When focus EditText, set the default text = prefix (ex: VND) <br/>
     * + When EditText lose focus, set the default text = "", EditText will display hint (ex:VND)
     */
    private fun handleCaseCurrencyEmpty(focused: Boolean) {
        if (focused) {
            if (text.toString().isEmpty()) {
                //setText(prefix);
            }
        } else {
            if (text.toString() == prefix) {
                //setText("");
            }
        }
    }

    private class CurrencyTextWatcher(view: AppCompatEditText, var prefix: String = ""): TextWatcher {

        val editText = view
        var previousCleanString = "0"

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

        override fun afterTextChanged(p0: Editable?) {
            val str = p0.toString()
            if (str.length < prefix.length) {
                editText.setText(prefix)
                editText.setSelection(prefix.length)
                return
            }
            if (str == prefix) {
                return
            }
            // cleanString this the string which not contain prefix and ,
            val cleanString = str.replace(prefix, "").replace(Regex("[,]"), "")
            // for prevent afterTextChanged recursive call
            if (cleanString == previousCleanString || cleanString.isEmpty()) {
                return
            }
            previousCleanString = cleanString;

            val formattedString =
            if (cleanString.contains(".")) {
                formatDecimal(cleanString)
            } else {
                formatInteger(cleanString)
            }
            editText.removeTextChangedListener(this) // Remove listener
            editText.setText(formattedString)
            handleSelection()
            editText.addTextChangedListener(this) // Add back the listener
        }

        private fun formatInteger(str: String): String {
            val parsed = BigDecimal(str)
            val formatter = DecimalFormat("$prefix#,###", DecimalFormatSymbols(Locale.US))
            return formatter.format(parsed)
        }

        private fun formatDecimal(str: String): String {
            if (str == ".") {
                return "$prefix."
            }
            val parsed = BigDecimal(str)
            // example pattern VND #,###.00
            val formatter = DecimalFormat(prefix + "#,###." + getDecimalPattern(str), DecimalFormatSymbols(locale));
            formatter.roundingMode = RoundingMode.DOWN
            return formatter.format(parsed)
        }

        /**
         * It will return suitable pattern for format decimal
         * For example: 10.2 -> return 0 | 10.23 -> return 00, | 10.235 -> return 000
         */
        private fun getDecimalPattern(str: String): String {
            val decimalCount = str.length - str.indexOf(".") - 1
            val decimalPattern = StringBuilder()

            var i = 0
            while (i < decimalCount && i < MAX_DECIMAL) {
                decimalPattern.append("0")
                i++
            }
            return decimalPattern.toString()
        }

        private fun handleSelection() {
            if (editText.text?.length?:0 <= MAX_LENGTH) {
                editText.setSelection(editText.text?.length?:0)
            } else {
                editText.setSelection(MAX_LENGTH)
            }
        }

    }

    private fun getDecimalPattern(str: String): String {
        val decimalCount = str.length - str.indexOf(".") - 1
        val decimalPattern = StringBuilder()
            var i = 0
            while (i < decimalCount && i < MAX_DECIMAL) {
                decimalPattern.append("0")
                i++
            }
        return decimalPattern.toString()
    }

    private fun formatDecimal(str: String): String {
        if (str == ".") {
            return "$prefix."
        }
        val parsed = BigDecimal(str)
        // example pattern VND #,###.00
        val formatter = DecimalFormat(prefix + "#,###." + getDecimalPattern(str), DecimalFormatSymbols(locale))
        formatter.roundingMode = RoundingMode.DOWN
        return formatter.format(parsed)
    }

    private fun formatInteger(str: String): String {
        val parsed = BigDecimal(str)
        val formatter = DecimalFormat("$prefix#,###", DecimalFormatSymbols(locale))
        return formatter.format(parsed)
    }

    public fun setTextFormatted(text: CharSequence) {
        currencyTextWatcher.previousCleanString = text.toString()
        val cleanString = text.toString()
        val formattedString =
        if (cleanString.contains(".")) {
            formatDecimal(cleanString)
        } else {
            formatInteger(cleanString)
        }
        super.setText(formattedString)
    }

    public fun getCleanValue() = currencyTextWatcher.previousCleanString
    public fun setPrefix(prf: String) {
        prefix = prf
        currencyTextWatcher.prefix = prf
    }
}