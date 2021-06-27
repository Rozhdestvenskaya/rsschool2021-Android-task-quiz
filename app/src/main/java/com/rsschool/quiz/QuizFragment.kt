package com.rsschool.quiz

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.rsschool.quiz.databinding.FragmentQuizBinding


class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val question by lazy { arguments?.getSerializable(QUESTION_KEY) as Question }
    private val index by lazy { arguments?.getInt(INDEX_KEY, 0) }
    private val isLast by lazy { arguments?.getBoolean(IS_LAST_KEY, false) }
    private val theme by lazy { arguments?.getInt(THEME_KEY) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contextThemeWrapper = theme?.let { ContextThemeWrapper(activity, it) }

        contextThemeWrapper?.theme?.let { updateStatusBarTheme(it) }

        _binding = FragmentQuizBinding.inflate(inflater.cloneInContext(contextThemeWrapper),
            container, false)

        val view = binding.root

        binding.nextButton.isEnabled = false

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.nextButton.isEnabled = true

            if (activity is OnAnswerCheckedListener) {
                val button = group.findViewById<RadioButton>(checkedId)
                val answer = group.indexOfChild(button)
                index?.let { (activity as OnAnswerCheckedListener).onAnswerChecked(it, answer) }
            }
        }

        binding.nextButton.setOnClickListener {
            if (activity is Navigator) (activity as Navigator).moveNext()
        }

        binding.toolbar.setNavigationOnClickListener {
            if (activity is Navigator) (activity as Navigator).movePrev()
        }

        binding.previousButton.setOnClickListener {
            if (activity is Navigator) (activity as Navigator).movePrev()
        }

        if (isLast == true) binding.nextButton.text = getString(R.string.submit)
        if (index == 0) {
            binding.previousButton.isEnabled = false
            binding.toolbar.navigationIcon = null
        }

        binding.toolbar.title = getString(R.string.count_question, index?.plus(1))
        binding.question.text = question.question

        for (i in 0 until binding.radioGroup.size) {
            val radioButton = binding.radioGroup.children.elementAt(i) as RadioButton
            radioButton.text = question.options[i]

            val current = arguments?.getInt(SAVED_ANSWER_KEY, -1)
            if (current != null && current >= 0 && current == i) {
                binding.radioGroup.check(radioButton.id)
            }
        }

        return view
    }

    private fun updateStatusBarTheme(theme: Resources.Theme) {
        //получаем цвет из ресурсов
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)
        val color: Int = ContextCompat.getColor(requireContext(), typedValue.resourceId)

        activity?.window?.statusBarColor = color
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val QUESTION_KEY = "QUESTION_KEY"
        private const val INDEX_KEY = "INDEX_KEY"
        private const val IS_LAST_KEY = "IS_LAST_KEY"
        private const val SAVED_ANSWER_KEY = "SAVED_ANSWER_KEY"
        private const val THEME_KEY = "THEME_KEY"

        fun createInstance(
            question: Question,
            index: Int,
            isLast: Boolean,
            savedAnswer: Int,
            theme: Int
        ): QuizFragment {
            val fragment = QuizFragment()
            val bundle = bundleOf(
                QUESTION_KEY to question,
                INDEX_KEY to index,
                IS_LAST_KEY to isLast,
                SAVED_ANSWER_KEY to savedAnswer,
                THEME_KEY to theme
            )
            fragment.arguments = bundle
            return fragment
        }
    }

}