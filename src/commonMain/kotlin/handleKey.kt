import com.soywiz.korev.Key
import com.soywiz.korev.MouseButton
import com.soywiz.korge.input.Input
import com.soywiz.korge.view.Stage

/**
 * キー入力のハンドリングを行う
 */
fun Stage.handleInputKey(input: Input) {
    val moveFront = input.keys.pressing(Key.W)
    val moveLeft = input.keys.pressing(Key.A)
    val moveBack = input.keys.pressing(Key.S)
    val moveRight = input.keys.pressing(Key.D)
    val launch = input.keys.justPressed(Key.SPACE)
    val leftRotate = input.mouseButtonPressed(MouseButton.LEFT)
    val rightRotate = input.mouseButtonPressed(MouseButton.RIGHT)
    if (moveFront) y -= 5
    if (moveLeft) x -= 5
    if (moveBack) y += 5
    if (moveRight) x += 5
    println("$moveFront $moveLeft $moveBack $moveRight $launch $leftRotate $rightRotate")
}
