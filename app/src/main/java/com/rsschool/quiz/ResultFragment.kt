package com.rsschool.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.rsschool.quiz.databinding.ResultBinding


class ResultFragment : Fragment() {
    private var _binding: ResultBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ResultBinding.inflate(inflater, container, false)
        val view = binding.root

        val questionCounter = arguments?.getInt(QUESTION_COUNTER_KEY)
        val correctAnswers = arguments?.getInt(CORRECT_ANSWERS_KEY)

        binding.textView.text = getString(R.string.result, correctAnswers, questionCounter)

        binding.back.setOnClickListener {
            if (activity is Navigator) (activity as Navigator).moveStart()
        }
        binding.share.setOnClickListener {
            if (activity is ShareResult) (activity as ShareResult).share()
        }
        binding.close.setOnClickListener {
            if (activity is AppInterraptor) (activity as AppInterraptor).exit()
        }

        return view
    }

    companion object {
        private const val QUESTION_COUNTER_KEY = "QUESTION_COUNTER_KEY"
        private const val CORRECT_ANSWERS_KEY = "CORRECT_ANSWERS_KEY"

        fun createInstance(questionCounter: Int, correctAnswers: Int): ResultFragment {
            val fragment = ResultFragment()
            val bundle = bundleOf(
                QUESTION_COUNTER_KEY to questionCounter,
                CORRECT_ANSWERS_KEY to correctAnswers
            )
            fragment.arguments = bundle
            return fragment
        }
    }
}