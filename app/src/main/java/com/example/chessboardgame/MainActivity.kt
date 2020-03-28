package com.example.chessboardgame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TextView and buttons
        val boardSize: TextView = findViewById(R.id.text_view_board_size)
        val numberOfMoves: TextView = findViewById(R.id.text_number_of_moves)
        val startGameButton: Button = findViewById(R.id.button_start_game)

        startGameButton.setOnClickListener {
            val intent = Intent(this, ChessBoardActivity::class.java)
            intent.putExtra("boardSize", boardSize.text.toString())
            intent.putExtra("numberOfMoves", numberOfMoves.text.toString())

            startActivity(intent)
        }
    }
}