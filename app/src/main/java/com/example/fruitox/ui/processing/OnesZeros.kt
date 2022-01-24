package com.example.fruitox.ui.processing

import processing.core.PApplet

class OnesZeros: PApplet() {

    //..................values

    lateinit var numbers: ArrayList<Numbers>

    override fun setup() {
        background(0f)
        numbers = ArrayList()
    }
    override fun draw() {

        frameRate(10f)

        if(numbers.size == 50){
            numbers.removeAt(0)
        }

        //Log.d("hillo", numbers.size.toString())

        newNum()

        for (n:Numbers in numbers){
            n.show()
        }

        background(0f,0f,0f,20f)
        //filter(BLUR, 1f)
    }

    fun newNum(){

        val x=random(width.toFloat())
        val y=random(-10f, 50f)

        numbers.add(Numbers(x, y))
    }

    inner class Numbers(x_: Float, y_: Float){

        var x = x_
        var y = y_
        var grow = 0f
        var num = "1"


        fun show(){
            textSize(30f)
            fill(0f, 255f, 0f)

            val randomValue = random(-50f, 50f)
            if (randomValue > 0) {
                num = "1"
            } else if (randomValue < 0) {
                num = "0"
            }
            if(grow+y <= height){
                text(num, x, y + grow)
            }
            grow += 30f
        }
    }
}