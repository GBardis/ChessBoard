package com.example.chessboardgame

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        // Initialize TextView and buttons
        val boardSize = findViewById<EditText>(R.id.text_view_board_size)
        val numberOfMoves = findViewById<EditText>(R.id.text_number_of_moves)
        val startGameButton = findViewById<Button>(R.id.button_start_game)

        initEditTextValidators(boardSize, numberOfMoves)

        startGameButton.setOnClickListener {
            if (checkValidations(boardSize, numberOfMoves)) {
                val intent = Intent(this, ChessBoardActivity::class.java)
                intent.putExtra("boardSize", boardSize.text.toString())
                intent.putExtra("numberOfMoves", numberOfMoves.text.toString())
                startActivity(intent)
            } else {
                showToastMessageForEmptyFields()
            }
        }
    }

    private fun checkValidations(
        boardSize: EditText,
        numberOfMoves: EditText
    ) =
        (!boardSize.text.isNullOrEmpty() || !numberOfMoves.text.isNullOrEmpty())
                && (boardSize.error.isNullOrEmpty() && numberOfMoves.error.isNullOrEmpty())

    private fun initEditTextValidators(
        boardSize: EditText,
        numberOfMoves: EditText
    ) {

        boardSize.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().toInt() !in 6..16) {
                    boardSize.error = getString(R.string.message_valid_board_size)
                }
            }
        })

        numberOfMoves.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().toInt() < 3) {
                    numberOfMoves.error = getString(R.string.message_valid_number_of_moves)
                }
            }
        })
    }

    private fun showToastMessageForEmptyFields() {
        Toast.makeText(
            applicationContext,
            getString(R.string.message_validations),
            Toast.LENGTH_LONG
        ).show()
    }
}
