package com.example.chessboardgame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chessboardgame.ShortestPath.cellPath

class MainActivity : AppCompatActivity() {
    lateinit var boardSize: EditText
    lateinit var numberOfMoves: EditText
    lateinit var startGameButton: Button
    lateinit var continueGameButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        boardSize = findViewById(R.id.text_view_board_size)
        numberOfMoves = findViewById(R.id.text_number_of_moves)
        startGameButton = findViewById(R.id.button_start_game)
        continueGameButton = findViewById(R.id.button_continue_game)

        initEditTextValidators()

        startGameButton.setOnClickListener {
            if (checkValidations()) {
                val intent = Intent(this, ChessBoardActivity::class.java)
                intent.putExtra("boardSize", boardSize.text.toString().toInt())
                intent.putExtra("numberOfMoves", numberOfMoves.text.toString().toInt())
                intent.putExtra("newGame", true)
                clearPreferences()
                startActivity(intent)
            } else {
                showToastMessageForEmptyFields()
            }
        }

        continueGameButton.setOnClickListener {
            checkIfGameExists()
        }
    }

    private fun checkIfGameExists() {
        val sharedPreferences = getSharedPreferences(
            "ChessBoardPref",
            Context.MODE_PRIVATE
        )

        if (!sharedPreferences.getBoolean("newGame", true)) {
            val intent = Intent(this, ChessBoardActivity::class.java)
            intent.putExtra("newGame", false)
            startActivity(intent)
        } else {
            showToastMessageNoGameFound()
        }
    }

    private fun clearPreferences(){
        val settings: SharedPreferences =
            getSharedPreferences("ChessBoardPref", Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        cellPath.clear()
    }

    private fun checkValidations() =
        (!boardSize.text.isNullOrEmpty() || !numberOfMoves.text.isNullOrEmpty())
                && (boardSize.error.isNullOrEmpty() && numberOfMoves.error.isNullOrEmpty())

    private fun initEditTextValidators() {
        boardSize.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() || s.toString().toInt() !in 6..16) {
                    boardSize.error = getString(R.string.message_valid_board_size)
                }
            }
        })

        numberOfMoves.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() || s.toString().toInt() < 3) {
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

    private fun showToastMessageNoGameFound() {
        Toast.makeText(
            applicationContext,
            getString(R.string.message_no_game_found),
            Toast.LENGTH_LONG
        ).show()
    }
}