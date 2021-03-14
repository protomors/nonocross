/**This file is part of Nonocross.

Nonocross is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Nonocross is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Nonocross.  If not, see <https://www.gnu.org/licenses/>.*/
package com.picross.nonocross.views.grid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import com.picross.nonocross.R

class Cell(
    val row: Int,
    val col: Int,
    private val cellLength: Int,
    bigPadding: Int,
    context: Context
) {

    var userShade = CellShade.EMPTY

    // Each Cell has at least 0.5px padding but some have 1.5px
    enum class BigPadding(val flag: Int) {
        LEFT(0x8), TOP(0x4), RIGHT(0x2), BOTTOM(0x1)
    }

    private val top = 1 + row.toFloat() * (cellLength + 1) + 2 * (row.toFloat() / 5)
    private val left = 1 + col.toFloat() * (cellLength + 1) + 2 * (col.toFloat() / 5)
    private val right = left + cellLength
    private val bottom = top + cellLength

    private val topBound = top - if (bigPadding and BigPadding.TOP.flag != 0) 1.5 else 0.5
    private val leftBound = left - if (bigPadding and BigPadding.LEFT.flag != 0) 1.5 else 0.5
    private val rightBound = right + if (bigPadding and BigPadding.RIGHT.flag != 0) 1.5 else 0.5
    private val bottomBound = bottom - if (bigPadding and BigPadding.BOTTOM.flag != 0) 1.5 else 0.5

    private val colorCross = ResourcesCompat.getColor(context.resources, R.color.colorCross, null)
    private val colorShade = ResourcesCompat.getColor(context.resources, R.color.colorShade, null)
    private val colorEmpty = ResourcesCompat.getColor(context.resources, R.color.colorEmpty, null)

    private val paintEmpty = Paint().apply { color = colorEmpty }
    private val paintShade = Paint().apply { color = colorShade }
    private val paintCross = Paint().apply { color = colorCross }
        .apply { strokeCap = Paint.Cap.ROUND }
        .apply { isAntiAlias = true }

    fun draw(canvas: Canvas) {
        when (userShade) {
            CellShade.EMPTY -> canvas.drawRect(left, top, right, bottom, paintEmpty)
            CellShade.SHADE -> canvas.drawRect(left, top, right, bottom, paintShade)
            CellShade.CROSS -> drawCross(canvas)
        }
    }

    private fun drawCross(canvas: Canvas) {
        paintCross.apply { strokeWidth = cellLength / 15F }
        canvas.drawRect(left, top, right, bottom, paintEmpty)
        canvas.drawLine(
            left + cellLength * 0.25F,
            top + cellLength * 0.25F,
            left + cellLength * 0.75F,
            top + cellLength * 0.75F,
            paintCross
        )
        canvas.drawLine(
            left + cellLength * 0.25F,
            top + cellLength * 0.75F,
            left + cellLength * 0.75F,
            top + cellLength * 0.25F,
            paintCross
        )
    }

    fun isInside(x: Float, y: Float): Boolean {
        return x in leftBound..rightBound && y in topBound..bottomBound
    }

    fun click(invert: Boolean) {
        userShade = if (invert) {
            when (userShade) {
                CellShade.CROSS, CellShade.SHADE -> CellShade.EMPTY
                CellShade.EMPTY -> CellShade.SHADE
            }
        } else {
            when (userShade) {
                CellShade.CROSS -> CellShade.EMPTY
                CellShade.EMPTY, CellShade.SHADE -> CellShade.CROSS
            }
        }
    }

    enum class CellShade {
        CROSS,
        SHADE,
        EMPTY
    }
}