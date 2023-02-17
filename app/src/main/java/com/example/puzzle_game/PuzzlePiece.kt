package com.example.puzzle_game

import android.content.Context
import android.support.v7.widget.AppCompatImageView

class PuzzlePiece(context: Context?) : AppCompatImageView(context) {
    @JvmField
    var xCoord = 0
    @JvmField
    var yCoord = 0
    var pieceWidth = 0
    var pieceHeight = 0
    @JvmField
    var canMove = true
}