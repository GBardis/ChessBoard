package com.example.chessboardgame

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chessboardgame.ShortestPath.BFS
import kotlinx.android.synthetic.main.activity_chess_board.*


class ChessBoardActivity : AppCompatActivity() {
    private lateinit var boardCells: Array<Array<ImageView?>>
    private var startingPosition = Cell()
    private var endingPosition = Cell()
    private var boardSize: Int? = null
    private var numberOfMoves: Int? = null
    lateinit var resetButton: Button
    lateinit var calculatePathButton: Button
    var newGame: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess_board)
        initView()
        loadBoard()
    }

    private fun initView() {
        resetButton = findViewById(R.id.button_reset)
        calculatePathButton = findViewById(R.id.button_calculate_paths)
        newGame = intent.getBooleanExtra("newGame", false)

        if (newGame) {
            boardSize = intent.getIntExtra("boardSize", 6)
            numberOfMoves = intent.getIntExtra("numberOfMoves", 3)
            boardCells = Array(boardSize!!) { arrayOfNulls<ImageView>(boardSize!!) }
        } else {
            continueGame()
        }
        resetButton.setOnClickListener {
            resetValues()
        }

        calculatePathButton.setOnClickListener {
            // source coordinates
            val src = Cell(startingPosition.x, startingPosition.y)

            // destination coordinates
            val dest = Cell(endingPosition.x, endingPosition.y)

            println("Minimum number of steps required is " + BFS(src, dest, boardSize!!))
        }
    }

    override fun onResume() {
        super.onResume()
        if (!newGame) {
            getGamePreferences()
        }
    }

    override fun onPause() {
        super.onPause()
        saveGamePreferences()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveGamePreferences()
    }

    private fun saveGamePreferences() {
        newGame = false
        val sharedPreferences = getSharedPreferences(
            "ChessBoardPref",
            Context.MODE_PRIVATE
        )

        val myEdit = sharedPreferences.edit()

        myEdit.putInt(
            "boardSize",
            boardSize!!.toInt()
        )

        myEdit.putInt(
            "numberOfMoves",
            numberOfMoves!!.toInt()
        )

        myEdit.putBoolean(
            "newGame",
            newGame
        )

        myEdit.apply()
    }


    private fun continueGame() {
        if (!newGame) {
            getGamePreferences()
        }
    }


    private fun getGamePreferences() {
        val sh = getSharedPreferences(
            "ChessBoardPref",
            Context.MODE_PRIVATE
        )
        boardSize = sh.getInt("boardSize", 6)
        numberOfMoves = sh.getInt("numberOfMoves", 3)
        newGame = sh.getBoolean("newGame", false)
        boardCells = Array(boardSize!!) { arrayOfNulls<ImageView>(boardSize!!) }
    }


    private fun loadBoard() {
        for (i in boardCells.indices) {
            for (j in boardCells.indices) {
                boardCells[i][j] = ImageView(this)
                boardCells[i][j]?.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    width = 95
                    height = 85
                    bottomMargin = 5
                    topMargin = 5
                    leftMargin = 5
                    rightMargin = 5
                }
                boardCells[i][j]?.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimary
                    )
                )
                boardCells[i][j]?.setOnClickListener(CellClickListener(i, j))

                layout_board.addView(boardCells[i][j])
            }
        }
    }

    inner class CellClickListener(
        val i: Int,
        val j: Int
    ) : View.OnClickListener {

        override fun onClick(p0: View?) {
            val point = p0 as ImageView
            if (!startingPosition.assignImage && !hasDrawable(point)) {
                point.setImageResource(R.drawable.chess_horse)
                startingPosition.assignImage = true
                startingPosition.x = i
                startingPosition.y = j
            } else if (!endingPosition.assignImage && !hasDrawable(point)) {
                point.setImageResource(R.drawable.chess_waypoint)
                endingPosition.assignImage = true
                endingPosition.x = i
                endingPosition.y = j
            } else {
                showToastMessage()
            }
        }

    }

    private fun hasDrawable(imageView: ImageView): Boolean {
        return imageView.drawable != null
    }

    private fun showToastMessage() {
        Toast.makeText(
            applicationContext,
            getString(R.string.message_both_values_are_assigned),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun resetValues() {
        boardCells[startingPosition.x][startingPosition.y]?.setImageResource(0)
        boardCells[endingPosition.x][endingPosition.y]?.setImageResource(0)
        startingPosition.x = 0
        startingPosition.y = 0
        startingPosition.assignImage = false
        endingPosition.x = 0
        endingPosition.y = 0
        endingPosition.assignImage = false
    }
}
