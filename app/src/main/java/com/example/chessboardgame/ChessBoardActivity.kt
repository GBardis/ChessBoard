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
import com.example.chessboardgame.ShortestPath.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_chess_board.*
import kotlinx.coroutines.*
import java.lang.reflect.Type

class ChessBoardActivity : AppCompatActivity() {
    private lateinit var boardCells: Array<Array<ImageView?>>
    private var startingPosition = Cell()
    private var endingPosition = Cell()
    private var boardSize: Int? = null
    private var numberOfMoves: Int? = null
    lateinit var resetButton: Button
    lateinit var calculatePathButton: Button
    var newGame: Boolean = false
    private val gson = Gson()
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess_board)
        initView()
        if (newGame) {
            loadBoard()
        }
    }

    private fun initView() {
        resetButton = findViewById(R.id.button_reset)
        calculatePathButton = findViewById(R.id.button_calculate_paths)
        newGame = intent.getBooleanExtra("newGame", false)

        if (newGame) {
            boardSize = intent.getIntExtra("boardSize", 6)
            numberOfMoves = intent.getIntExtra("numberOfMoves", 3)
            boardCells = Array(boardSize!!) { arrayOfNulls<ImageView>(boardSize!!) }
        }

        resetButton.setOnClickListener {
            resetValues()
        }

        calculatePathButton.setOnClickListener {
            if (startingPosition.assignImage && endingPosition.assignImage) {
                uiScope.launch {
                    val thread = async(Dispatchers.IO) {
                        print(Thread.currentThread().name)
                        calculatePath()
                    }
                    thread.await()
                    printPath()
                }
            } else {
                showToastMessageForNoChessPoints()
            }
        }
    }

    private fun calculatePath() {
        // source coordinates
        val src = Cell(startingPosition.x, startingPosition.y)
        // destination coordinates
        val dest = Cell(endingPosition.x, endingPosition.y)

        val cell: Cell = BFS(src, dest, boardSize!!)
        println("Minimum number of steps required is " + cell.dist)
        print("The complete path is: ")
        createPath(cell)
    }

    private fun printPath() {
        for (cellPaths in cellPath) {
            boardCells[cellPaths.x][cellPaths.y]?.setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorPath)
            )
            layout_board.removeView(boardCells[cellPaths.x][cellPaths.y])
            layout_board.addView(boardCells[cellPaths.x][cellPaths.y])
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
        val sharedPreferences = getSharedPreferences(
            "ChessBoardPref", Context.MODE_PRIVATE
        )
        newGame = false

        val gEdit = sharedPreferences.edit()

        gEdit.putInt("boardSize", boardSize!!.toInt())

        gEdit.putInt("numberOfMoves", numberOfMoves!!.toInt())

        gEdit.putBoolean("newGame", newGame)

        gEdit.putString("SolvedPath", gson.toJson(cellPath))

        gEdit.putString("startingPosition", gson.toJson(startingPosition))

        gEdit.putString("endingPosition", gson.toJson(endingPosition))

        gEdit.apply()
    }

    private fun getGamePreferences() {
        val sharedPreferences = getSharedPreferences(
            "ChessBoardPref", Context.MODE_PRIVATE
        )
        val listType: Type = object : TypeToken<ArrayList<Cell?>?>() {}.type

        boardSize = sharedPreferences.getInt(
            "boardSize", 0
        )
        numberOfMoves = sharedPreferences.getInt(
            "numberOfMoves", 0
        )
        newGame = sharedPreferences.getBoolean(
            "newGame", false
        )

        cellPath = gson.fromJson(
            sharedPreferences.getString(
                "SolvedPath", ""
            ), listType
        )
        startingPosition = gson.fromJson(
            sharedPreferences.getString(
                "startingPosition", ""
            ), Cell::class.java
        )
        endingPosition = gson.fromJson(
            sharedPreferences.getString(
                "endingPosition", ""
            ), Cell::class.java
        )

        boardCells = Array(boardSize!!) { arrayOfNulls<ImageView>(boardSize!!) }

        loadBoard()
        if (cellPath.isNotEmpty()) {
            printPath()
            boardCells[startingPosition.x][startingPosition.y]?.setImageResource(R.drawable.chess_horse)
            boardCells[endingPosition.x][endingPosition.y]?.setImageResource(R.drawable.chess_waypoint)
        }

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
        private val i: Int,
        private val j: Int
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
                showToastMessageForChessPoints()
            }
        }
    }

    private fun hasDrawable(imageView: ImageView): Boolean {
        return imageView.drawable != null
    }

    private fun showToastMessageForChessPoints() {
        Toast.makeText(
            applicationContext,
            getString(R.string.message_both_pointers_are_assigned),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showToastMessageForNoChessPoints() {
        Toast.makeText(
            applicationContext,
            getString(R.string.message_pointers_are_not_assigned),
            Toast.LENGTH_LONG
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
        for (cellPaths in cellPath) {
            boardCells[cellPaths.x][cellPaths.y]?.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
            layout_board.removeView(boardCells[cellPaths.x][cellPaths.y])
            layout_board.addView(boardCells[cellPaths.x][cellPaths.y])
        }
    }
}
