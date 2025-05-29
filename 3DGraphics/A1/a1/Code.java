package a1;

import javax.swing.*;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import java.awt.event.KeyListener;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import java.awt.event.KeyEvent;

public class Code extends JFrame implements GLEventListener, KeyListener { 
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private boolean moveCircle = false;
	private int colorOption = 0;
	private int direction = 0;

	private float[][] triangleColor = {
		{0.0f, 0.0f, 1.0f, 1.0f},
		{0.0f, 0.0f, 1.0f, 1.0f},
		{0.0f, 0.0f, 1.0f, 1.0f},
	};
	private float[][] triangleVertices = {
		{0.4f, -0.25f, 0.0f, 1.0f},
		{-0.4f, -0.25f, 0.0f, 1.0f},
		{0.01f, 0.2f, 0.0f, 1.0f}
	};
	private float x = 0.0f;
	private float y = 0.0f;
	private float speed = 0.01f;
	private long lastFrameTime = 0;
	private float timeIncrement = speed;

	public Code() { 
		setTitle("Chapter 2 - program 6");
		setSize(400, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		myCanvas.addKeyListener(this);

		this.add(myCanvas);
		this.setVisible(true);

		Animator animator = new Animator(myCanvas);
		animator.start();

		lastFrameTime = System.currentTimeMillis();
	}

	public void display(GLAutoDrawable drawable) { 
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - lastFrameTime; // Time elapsed since last call to display()
		lastFrameTime = currentTime;

		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(renderingProgram);

		moveTriangle(elapsedTime);

		int offsetLocX = gl.glGetUniformLocation(renderingProgram, "offsetx");
		int offsetLocY = gl.glGetUniformLocation(renderingProgram, "offsety");
		gl.glProgramUniform1f(renderingProgram, offsetLocX, x);
		gl.glProgramUniform1f(renderingProgram, offsetLocY, y);

		int v1SizeLoc = gl.glGetUniformLocation(renderingProgram, "v1Size");
		int v2SizeLoc = gl.glGetUniformLocation(renderingProgram, "v2Size");
		int v3SizeLoc = gl.glGetUniformLocation(renderingProgram, "v3Size");
		gl.glProgramUniform4fv(renderingProgram, v1SizeLoc, 1, triangleVertices[0], 0);
		gl.glProgramUniform4fv(renderingProgram, v2SizeLoc, 1, triangleVertices[1], 0);
		gl.glProgramUniform4fv(renderingProgram, v3SizeLoc, 1, triangleVertices[2], 0);

		// Set the color of the triangle, each vertice needs to be accessible
		int v1ColorLoc = gl.glGetUniformLocation(renderingProgram, "v1Color");
		int v2ColorLoc = gl.glGetUniformLocation(renderingProgram, "v2Color");
		int v3ColorLoc = gl.glGetUniformLocation(renderingProgram, "v3Color");
		gl.glProgramUniform4fv(renderingProgram, v1ColorLoc, 1, triangleColor[0], 0);
		gl.glProgramUniform4fv(renderingProgram, v2ColorLoc, 1, triangleColor[1], 0);
		gl.glProgramUniform4fv(renderingProgram, v3ColorLoc, 1, triangleColor[2], 0);

		gl.glDrawArrays(GL_TRIANGLES,0,3);
	}

	private void moveTriangle(long elapsedTime) {
		if (!moveCircle) {
			if (elapsedTime > 0) {
				x += (float) (elapsedTime / 15) * speed;
				if (x > 1.0f) speed *= -1.0f;
				if (x < -1.0f) speed *= -1.0f;
			}
		}

		if (moveCircle) {
			// Move the triangle in a circle around the screen
			float radius = 0.5f; // Size of the circle the triangle moves around
			timeIncrement += elapsedTime / 10;
			float angle = (float) (timeIncrement % 36000);

			// Math.toRadians converts degrees to radians, triangle stutters without it
			x = (float) (radius * Math.cos(Math.toRadians(angle)));
			y = (float) (radius * Math.sin(Math.toRadians(angle)));
		}
	}

	public void init(GLAutoDrawable drawable) { 
		GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram = Utils.createShaderProgram("a1/vertShader.glsl", "a1/fragShader.glsl");
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);

		Package pkg = Package.getPackage("com.jogamp.opengl");
		System.out.println("OpenGL Version: " + gl.glGetString(GL4.GL_VERSION));
		System.out.println("JOGL version: " + Package.getPackage("com.jogamp.opengl").getImplementationVersion());
		System.out.println("Java version: " + System.getProperty("java.version"));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_1: // Switch movment
				moveCircle = !moveCircle;
				System.out.println("Changing movement");
			break;

			case KeyEvent.VK_2: // Change color
				colorOption++;
				if (colorOption > 3) colorOption = 0;

				cycleColor(colorOption);
				System.out.println("Changing to color option: " + colorOption);
			break;

			case KeyEvent.VK_3: // Increase size
				increaseVerticesSize();
			break;

			case KeyEvent.VK_4: // Decrease size
				decreaseVerticesSize();
			break;

			case KeyEvent.VK_5: // Cycle direction
				direction += 90;
				if (direction >= 360) direction = 0;
				cycleDirection();
			break;

			default:
			break;
		}
	}

	private void increaseVerticesSize() {
		for (int i = 0; i <= 2; i++) {
			triangleVertices[i][0] *= 1.08f;
			triangleVertices[i][1] *= 1.08f;
		}
	}

	private void decreaseVerticesSize() {
		for (int i = 0; i <= 2; i++) {
			triangleVertices[i][0] *= 0.8f;
			triangleVertices[i][1] *= 0.8f;
		}
	}

	private void cycleDirection() {
		System.out.println("Rotating to: " + direction);

		// Find the center point
		// ( (x1 + x2 + x3) / 3, (y1 + y2 + y3) / 3 )
		float xCenter = ((triangleVertices[0][0] + triangleVertices[1][0] + triangleVertices[2][0]) / 3);
		float yCenter = ((triangleVertices[0][1] + triangleVertices[1][1] + triangleVertices[2][1]) / 3);

		// Rotate the triangle
		//x' = (x - x_c) * cos(θ) - (y - y_c) * sin(θ) + x_c
		//y' = (x - x_c) * sin(θ) + (y - y_c) * cos(θ) + y_c 
		int theta = direction;

		// If direction == 0, reset the triangle manually
		// Zero would not work with my equations, so this works better
		if (direction == 0) {
			// Subtract the center point from each vertice to keep the triangle centered
			for (int i = 0; i <= 2; i++) {
				triangleVertices[i][0] -= xCenter;
				triangleVertices[i][1] -= yCenter;
			}

			// Rotate the triangle
			triangleVertices[0][0] = Math.abs(triangleVertices[0][0]);
			triangleVertices[0][1] = -Math.abs(triangleVertices[0][1]);

			triangleVertices[1][0] = -Math.abs(triangleVertices[1][0]);
			triangleVertices[1][1] = -Math.abs(triangleVertices[1][1]);

			triangleVertices[2][0] = Math.abs(triangleVertices[2][0]);
			triangleVertices[2][1] = Math.abs(triangleVertices[2][1]);

		} else {
			for (int i = 0; i <= 2; i++) {
			float xNew = (float) ((triangleVertices[i][0] - xCenter) * Math.cos(Math.toRadians(theta)) - (triangleVertices[i][1] - yCenter) * Math.sin(Math.toRadians(theta)) + xCenter);
			float yNew = (float) ((triangleVertices[i][0] - xCenter) * Math.sin(Math.toRadians(theta)) + (triangleVertices[i][1] - yCenter) * Math.cos(Math.toRadians(theta)) + yCenter);
			triangleVertices[i][0] = xNew;
			triangleVertices[i][1] = yNew;
			}
		}	
	}

	private void cycleColor(int colorOption) {
		switch (colorOption) {
			case 0: // Blue
				triangleColor[0] = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
				triangleColor[1] = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
				triangleColor[2] = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
			break;

			case 1: // Yellow
				triangleColor[0] = new float[]{1.0f, 1.0f, 0.0f, 1.0f};
				triangleColor[1] = new float[]{1.0f, 1.0f, 0.0f, 1.0f};
				triangleColor[2] = new float[]{1.0f, 1.0f, 0.0f, 1.0f};
			break;

			case 2: // Purple
				triangleColor[0] = new float[]{0.5f, 0.0f, 1.0f, 1.0f};
				triangleColor[1] = new float[]{0.5f, 0.0f, 1.0f, 1.0f};
				triangleColor[2] = new float[]{0.5f, 0.0f, 1.0f, 1.0f};
			break;

			case 3: // Gradient of red/green/blue
				triangleColor[0] = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
				triangleColor[1] = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
				triangleColor[2] = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
			break;

			default:
			break;
		}
	}

	public static void main(String[] args) { new Code(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}

	@Override
	public void keyTyped(KeyEvent e) { } 
	@Override
	public void keyReleased(KeyEvent e) { }
}