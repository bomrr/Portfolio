package a2;

import java.nio.*;
import java.lang.Math;
import javax.swing.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_MIRRORED_REPEAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.common.nio.Buffers;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.joml.*;

public class Code extends JFrame implements GLEventListener, KeyListener {
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int axesProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[10];
	private float cubeLocX, cubeLocY, cubeLocZ;
	private float pyrLocX, pyrLocY, pyrLocZ;
	private float pryRotX, pryRotY, pryRotZ;
	private float tableX, tableY, tableZ;
	private float diamondX, diamondY, diamondZ;
	private float cupX, cupY, cupZ;
	private float axesX, axesY, axesZ;
	private long lastFrameTime = System.currentTimeMillis();
	private long currentTime;
	private long elapsedTime;
	private int cubeSpeed = 1;
	private int cubeDirection = 1;
	private int pyrSpeed = 1;
	
	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f();  // perspective matrix
	private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private int mvLoc, pLoc;
	private float aspect;
	private boolean axes = true;

	// Camera variables
	private float cameraX, cameraY, cameraZ;
	private Vector3f cameraU = new Vector3f();
	private Vector3f cameraV = new Vector3f();
	private Vector3f cameraN = new Vector3f();
	private Matrix4f rMatrix = new Matrix4f();
	private Matrix4f tMatrix = new Matrix4f();
	private Matrix4f vMatrix = new Matrix4f();
	private Matrix4f rotatorMatrix = new Matrix4f();

	// Cup importing variables
	private ImportedModel coffeeCupModel;

	// Textures
	private int pyramidTexture;
	private int slidingCubeTexture;
	private int tableTexture;
	private int coffeeCupTexture;
	private int diamondTexture;

	/*
	 * In the constructor, setTitle, setSize
	 * 
	 *  myCanvas is the frame to draw on
	 */
	public Code() {
		setTitle("Chapter 4 - program 3");
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		myCanvas.addKeyListener(this);

		this.add(myCanvas);
		this.setVisible(true);

		/*
		 * Animator loops through repeatedly, redrawing the canvas over and over
		 */
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	/*
	 * What will be drawn each frame
	 * Called once per frame
	 */
	public void display(GLAutoDrawable drawable) {
		// Compute the time since last display call
		currentTime = System.currentTimeMillis();
		elapsedTime = currentTime - lastFrameTime;
		lastFrameTime = currentTime;

		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);

		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		updateCamera();

		// Draw each object in vbo
		drawObject(gl, 0, cubeLocX, cubeLocY, cubeLocZ, 36, slidingCubeTexture); // Cube
		drawObject(gl, 2, pyrLocX, pyrLocY, pyrLocZ, 18, pyramidTexture); // Pyramid
		drawObject(gl, 4, tableX, tableY, tableZ, 36, tableTexture); // Table
		drawObject(gl, 6, diamondX, diamondY, diamondZ, 24, diamondTexture); // Diamond
		drawObject(gl, 8, cupX, cupY, cupZ, coffeeCupModel.getNumVertices(), coffeeCupTexture); // Coffee cup

		moveAndManipulateCube();
		rotateTriangle();

		if (axes) {
			drawAxes(gl);
		}
	}

	private void updateCamera() {
		tMatrix = new Matrix4f();
		// setTranslation makes a translation matrix as described in the Appendix (thanks google)
		tMatrix.setTranslation(-cameraX, -cameraY, -cameraZ);

		// Set the rmatrix to the camera's orientation in the left-handed orientation
		// Fun fact: I was originally making this in the wrong way. I had formatted it to look like a matrix.
		// Then the professor said that the function takes it in a different way, and it took me another few hours
		// of debugging to realize that was the issue. Welcome to programming!
		rMatrix = new Matrix4f().set(
		cameraU.x, cameraV.x, -cameraN.x, 0.0f,
		cameraU.y, cameraV.y, -cameraN.y, 0.0f,
		cameraU.z, cameraV.z, -cameraN.z, 0.0f,
		0.0f, 0.0f,  0.0f, 1.0f
		); // You basically have to invert the matrix when manually coding it like this.

		vMatrix.identity().mul(rMatrix).mul(tMatrix);
	}

	public void drawObject(GL4 gl, int vboNumber, float x, float y, float z, int numVertices, int texture) {
		mMat.translation(x, y, z);

		// The triangle needs to be able to rotate
		if (vboNumber == 2) {
			mMat.rotateXYZ((float) Math.toRadians(pryRotX), (float) Math.toRadians(pryRotY), (float) Math.toRadians(pryRotZ));
		}

		// Reset the matrix to identity
		mvMat.identity();
		// Multiply by the m and v matrices to make the mv matrix
		mvMat.mul(vMatrix);
		mvMat.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Bind the appropriate VBO for positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vboNumber]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		try {
			// Apply the appropriate VBO for the texture
			// In future assignments, perhaps create a list of textures versus object coordinates? May make modification easier and more dynamic
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vboNumber + 1]);
			gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(1);

		} catch(Exception e) {
			// Do nothing, as the object has not had a texture made yet
		}

		// Apply the inputted texture
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, texture);

		if (texture == tableTexture) {
			// If the object is the diamond, apply mirrored repeat to the texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
		}

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numVertices);
	}

	public void drawAxes(GL4 gl) {
		mMat.translation(axesX, axesY, axesZ);

		mvMat.identity();
		mvMat.mul(vMatrix);
		mvMat.mul(mMat);

		gl.glUseProgram(axesProgram);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glDrawArrays(GL_LINES, 0, 6);
	}

	public void moveAndManipulateCube() {
		cubeLocZ += cubeSpeed * elapsedTime / 1000.0f * cubeDirection;

		// If the cube reaches the end of the table, change direction
		if (cubeLocZ > 1.0f || cubeLocZ < -3.0f) {
			cubeDirection *= -1;
		}
	}

	public void rotateTriangle() {
		pryRotY += pyrSpeed * elapsedTime / 10.0f;

		if (pryRotY > 360.0f) {
			pryRotY = pryRotY - 360.0f;
		}
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram = Utils.createShaderProgram("a2/vertShader.glsl", "a2/fragShader.glsl");
		axesProgram = Utils.createShaderProgram("a2/axesVertShader.glsl", "a2/axesFragShader.glsl");

		coffeeCupModel = new ImportedModel("CoffeCup.obj");
		pyramidTexture = Utils.loadTexture("PyramidTexture.jpg");
		slidingCubeTexture = Utils.loadTexture("SlidingCubeTexture.jpg");
		coffeeCupTexture = Utils.loadTexture("CoffeCupTexture.png");
		tableTexture = Utils.loadTexture("TableTexture.jpg");
		diamondTexture = Utils.loadTexture("DiamondTexture.jpg");

		setupVertices();
		setupCamera();

		cubeLocX = 2.0f; cubeLocY = -1.5f; cubeLocZ = -2.0f;
		pyrLocX = -2.0f; pyrLocY = -1.5f; pyrLocZ = -2.0f;
		pryRotX = 0.0f; pryRotY = 0.0f; pryRotZ = 0.0f;
		tableX = 0.0f; tableY = -3.0f; tableZ = -1.0f;
		diamondX = -3.5f; diamondY = -1.5f; diamondZ = 0.5f;
		cupX = -1.0f; cupY = -2.52f; cupZ = -1.0f;
		axesX = 0.0f; axesY = 0.0f; axesZ = 0.0f;
	}

	public void setupCamera() {
		cameraU.set(1.0f, 0.0f, 0.0f);
		cameraV.set(0.0f, 1.0f, 0.0f);
		cameraN.set(0.0f, 0.0f, -1.0f);

		cameraX = 0.0f; cameraY = 1.0f; cameraZ = 8.0f;
	}

	private void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		// Cube = 0
		float[] cubePositions = {
			-1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f, -1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f, 1.0f,  1.0f, -1.0f, 1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f
		};

		// Cube texture = 1
			// Correct texture coordinates for each face of the cube
			float[] cubeTextureCoords = {
				// Back face
				0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
				1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
				// Right face
				1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
				1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
				// Front face
				1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				// Left face
				1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				// Bottom face
				0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
				// Top face
				0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f
			};
		
		// Pyramid = 2
		float[] pyramidPositions = {
			-1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f,    //front
			1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 0.0f, -1.0f, 0.0f,    //right
			1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 0.0f, -1.0f, 0.0f,  //back
			-1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f,  //left
			-1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f,   //LF
			1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f    //RR
		};

		// Pyramid texture = 3
		float[] pyramidTextureCoords = {
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
		};
		
		// Table = 4
		float[] tablePositions = {
			-5.0f,  0.5f, -3.0f, -5.0f, -0.5f, -3.0f, 5.0f, -0.5f, -3.0f,
			5.0f, -0.5f, -3.0f, 5.0f,  0.5f, -3.0f, -5.0f,  0.5f, -3.0f,
			5.0f, -0.5f, -3.0f, 5.0f, -0.5f,  3.0f, 5.0f,  0.5f, -3.0f,
			5.0f, -0.5f,  3.0f, 5.0f,  0.5f,  3.0f, 5.0f,  0.5f, -3.0f,
			5.0f, -0.5f,  3.0f, -5.0f, -0.5f,  3.0f, 5.0f,  0.5f,  3.0f,
			-5.0f, -0.5f,  3.0f, -5.0f,  0.5f,  3.0f, 5.0f,  0.5f,  3.0f,
			-5.0f, -0.5f,  3.0f, -5.0f, -0.5f, -3.0f, -5.0f,  0.5f,  3.0f,
			-5.0f, -0.5f, -3.0f, -5.0f,  0.5f, -3.0f, -5.0f,  0.5f,  3.0f,
			-5.0f, -0.5f,  3.0f,  5.0f, -0.5f,  3.0f,  5.0f, -0.5f, -3.0f,
			5.0f, -0.5f, -3.0f, -5.0f, -0.5f, -3.0f, -5.0f, -0.5f,  3.0f,
			-5.0f,  0.5f, -3.0f, 5.0f,  0.5f, -3.0f, 5.0f,  0.5f,  3.0f,
			5.0f,  0.5f,  3.0f, -5.0f,  0.5f,  3.0f, -5.0f,  0.5f, -3.0f
		};

		// Table texture = 5
		float[] tableTextureCoords = {
			// Back face
			0.0f, 2.0f, 0.0f, 0.0f, 2.0f, 0.0f,
			2.0f, 0.0f, 2.0f, 2.0f, 0.0f, 2.0f,
			// Right face
			2.0f, 0.0f, 0.0f, 0.0f, 2.0f, 2.0f,
			2.0f, 0.0f, 2.0f, 2.0f, 0.0f, 2.0f,
			// Front face
			2.0f, 0.0f, 0.0f, 0.0f, 2.0f, 2.0f,
			0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f,
			// Left face
			2.0f, 0.0f, 0.0f, 0.0f, 2.0f, 2.0f,
			0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f,
			// Bottom face
			0.0f, 0.0f, 2.0f, 0.0f, 2.0f, 2.0f,
			0.0f, 0.0f, 2.0f, 2.0f, 0.0f, 2.0f,
			// Top face
			0.0f, 0.0f, 2.0f, 0.0f, 2.0f, 2.0f,
			2.0f, 2.0f, 0.0f, 2.0f, 0.0f, 0.0f
		};

		// Diamond = 6
		// A diamond shape
		float[] diamondPositions = {
			0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // Front top left
			0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // Front top right
			0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // Front bottom left
			0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // Front bottom right
			0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // Back top left
			0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // Back top right
			0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, // Back bottom left
			0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f // Back bottom right
		};

		// Diamond texture = 7
		float[] diamondTextureCoords = {
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
		};

		// Coffee cup = 8
		// Import the coffee cup vertices
		Vector3f[] cupVertices = coffeeCupModel.getVertices();
		Vector2f[] cupTexCoords = coffeeCupModel.getTexCoords();

		// Coffee cup texture = 9
		// Convert the cup vertices to a float array
		float[] cupPositions = new float[cupVertices.length * 3];
		float[] cupTextureCoords = new float[cupVertices.length * 2];

		for (int i = 0; i < cupVertices.length; i++) {
			cupPositions[i * 3] = cupVertices[i].x;
			cupPositions[i * 3 + 1] = cupVertices[i].y;
			cupPositions[i * 3 + 2] = cupVertices[i].z;
			cupTextureCoords[i * 2] = cupTexCoords[i].x;
			cupTextureCoords[i * 2 + 1] = cupTexCoords[i].y;
		}

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		// Cube
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(cubePositions);
		gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit()*4, cubeBuf, GL_STATIC_DRAW);

		// Cube texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer cTexBuf = Buffers.newDirectFloatBuffer(cubeTextureCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, cTexBuf.limit()*4, cTexBuf, GL_STATIC_DRAW);
		
		// Pyramid
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(pyramidPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);

		// Pyramid texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer pTexBuf = Buffers.newDirectFloatBuffer(pyramidTextureCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, pTexBuf.limit()*4, pTexBuf, GL_STATIC_DRAW);

		// Table
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer tableBuf = Buffers.newDirectFloatBuffer(tablePositions);
		gl.glBufferData(GL_ARRAY_BUFFER, tableBuf.limit()*4, tableBuf, GL_STATIC_DRAW);

		// Table texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer tableTexBuf = Buffers.newDirectFloatBuffer(tableTextureCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, tableTexBuf.limit()*4, tableTexBuf, GL_STATIC_DRAW);

		// Diamond
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer diamBuf = Buffers.newDirectFloatBuffer(diamondPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, diamBuf.limit()*4, diamBuf, GL_STATIC_DRAW);

		// Diamond texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer diamTexBuf = Buffers.newDirectFloatBuffer(diamondTextureCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, diamTexBuf.limit()*4, diamTexBuf, GL_STATIC_DRAW);

		// Coffee cup
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		FloatBuffer cupBuf = Buffers.newDirectFloatBuffer(cupPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, cupBuf.limit()*4, cupBuf, GL_STATIC_DRAW);

		// Coffee cup texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		FloatBuffer cupTexBuf = Buffers.newDirectFloatBuffer(cupTextureCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, cupTexBuf.limit()*4, cupTexBuf, GL_STATIC_DRAW);
	}

	public static void main(String[] args) {
		new Code();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

	public void dispose(GLAutoDrawable drawable) {}

	/*
	 * w / s - move the camera forward / backward a small amount (i.e. in the positive / negative N direction).
	 * a / d - move the camera left / right a small amount (i.e., in the negative / positive U direction)
	 * q / e - move the camera up / down a small amount (i.e., in the positive / negative V direction).
	 * left and right arrow - rotate the camera by a small amount left/right around its V axis ("pan").
	 * up and down arrow - rotate the camera by a small amount up/down around its U axis ("pitch").
	 * space bar - toggle the visibility of the world axes.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// Switch case for the camera movement
		switch (e.getKeyCode()) {
			// Camera needs to move based on the direction it is facing, not the world axes
			// Moving the camera: New position = old position + (speed * camera(orientation))
			case KeyEvent.VK_W:
			moveCamera(cameraN, 0.5f);
			break;

			case KeyEvent.VK_S:
			moveCamera(cameraN, -0.5f);
			break;

			case KeyEvent.VK_A:
			moveCamera(cameraU, -0.5f);
			break;

			case KeyEvent.VK_D:
			moveCamera(cameraU, 0.5f);
			break;

			case KeyEvent.VK_Q:
			moveCamera(cameraV, 0.5f);
			break;

			case KeyEvent.VK_E:
			moveCamera(cameraV, -0.5f);
			break;

			case KeyEvent.VK_LEFT:
			rotateCamera(0.5f, cameraV);
			break;

			case KeyEvent.VK_RIGHT:
			rotateCamera(-0.5f, cameraV);
			break;

			case KeyEvent.VK_UP:
			rotateCamera(0.5f, cameraU);
			break;

			case KeyEvent.VK_DOWN:
			rotateCamera(-0.5f, cameraU);
			break;

			case KeyEvent.VK_SPACE:
			axes = !axes;
			break;
		}
	}

	public void moveCamera(Vector3f cameraVector, float toMove) {
		// Forward C’ = C + (a*N)
		// Backward C’ = C - (a*N)
		// Right C’ = C + (a*U)
		// Left C - (a*U)
		// Up C’ = C + (a*V)
		// Down C’ = C - (a*V)
		cameraX += toMove * cameraVector.x;
		cameraY += toMove * cameraVector.y;
		cameraZ += toMove * cameraVector.z;
	}

	private void rotateCamera(float angle, Vector3f axis) {
		// Make the rotator matrix into an identity matrix
		rotatorMatrix.identity();
		rotatorMatrix.rotate((float) Math.toRadians(angle), axis);

		rotatorMatrix.transformDirection(cameraU);
		rotatorMatrix.transformDirection(cameraV);
		rotatorMatrix.transformDirection(cameraN);

		cameraU.normalize();
		cameraV.normalize();
		cameraN.normalize();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent e) {}
}