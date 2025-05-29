package a3;

import java.nio.*;

import javax.swing.*;
import java.lang.Math;

import static com.jogamp.opengl.GL2GL3.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static com.jogamp.opengl.GL.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.common.nio.Buffers;
import org.joml.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Code extends JFrame implements GLEventListener, KeyListener {
	private GLCanvas myCanvas;
	private int renderingProgram, skyRenderProgram, axesRenderProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[14];

	private float amt = 0.0f;
	private double prevTime;
	private double elapsedTime;
	
	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f();  // perspective matrix
	private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f mvMat = new Matrix4f(); // Model-view matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mLoc, vLoc, pLoc, nLoc, mvLoc;
	private int globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, mambLoc, mdiffLoc, mspecLoc, mshiLoc;
	private int skyboxTexture, borgCubeTexture, ufoTexture, lightCubeTexture, moonTexture;
	private float aspect;
	private Vector4f currentLightPos = new Vector4f();
	private float[] lightPos = new float[3];

	// Axes variables
	private boolean axesEnabled = true;
	private Matrix4f mMatAxes = new Matrix4f();
	private Matrix4f mvMatAxes = new Matrix4f(); // Model-view matrix

	// Stack variables
	private Matrix4fStack mvStack = new Matrix4fStack(10);

	// Camera variables
	private Vector3f cameraLoc = new Vector3f(3.0f, 2.5f, 6.5f);
	Camera camera = new Camera(cameraLoc);
	private Vector3f cameraU, cameraV, cameraN;

	// Positional light variables
	private Vector3f initialLightLoc = new Vector3f(8.0f, 2.0f, 2.0f);
	private boolean isOn = true;
	private Vector4f lightWorldPos = new Vector4f();
	private Vector4f lightVPos = new Vector4f();

	// Light cube variables
	private Vector3f lightCubePos = new Vector3f(6.0f, 3.0f, 0.5f);

	// Ufo variables
	private ImportedModel ufoModel;

	// Moon variables
	private Sphere mySphere;
	private int numSphereVerts;

	// white light properties
	float[] globalAmbient = new float[] { 0.8f, 0.8f, 0.8f, 1.0f };
	float[] lightAmbient = new float[] { 0.1f, 0.1f, 0.1f, 1.0f };
	float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] lightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		
	// gold material
	float[] goldAmb = Utils.goldAmbient();
	float[] goldDif = Utils.goldDiffuse();
	float[] goldSpe = Utils.goldSpecular();
	float goldShi = Utils.goldShininess();

	// Silver material
	float[] silvAmb = Utils.silverAmbient();
	float[] silvDif = Utils.silverDiffuse();
	float[] silvSpe = Utils.silverSpecular();
	float silvShi = Utils.silverShininess();

	// Moon material; silver with little specular
	float[] moonAmb = Utils.silverAmbient();
	float[] moonDif = Utils.silverDiffuse();
	float[] moonSpe = new float[] { 0.1f, 0.1f, 0.1f, 1.0f };
	float moonShi = 0.5f;


	// White material
	float[] whiteAmb = { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] whiteDif = { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] whiteSpe = { 1.0f, 1.0f, 1.0f, 1.0f };
	float whiteShi = 100.0f;

	public Code() {
		setTitle("a3 - They stole the moon!");
		setSize(800, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		myCanvas.addKeyListener(this);

		this.add(myCanvas);
		this.setVisible(true);

		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(renderingProgram);

		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");
		
		vMat.translation(-cameraLoc.x(), -cameraLoc.y(), -cameraLoc.z());

		mMat.rotateX((float)Math.toRadians(35.0f));

		// Camera ==========================================================================
		camera.updateCamera();
		cameraU = camera.getCameraU();
		cameraV = camera.getCameraV();
		cameraN = camera.getCameraN();
		cameraLoc = camera.getCameraLoc();

		camera.setCameraLoc(cameraLoc);
		vMat.set(camera.getvMat());

		// Place the v-matrix in the stack
		mvStack.pushMatrix();
		mvStack.mul(vMat);

		// Set up a seperate model-view matrix for independent objects
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		// Light ==========================================================================
		currentLightPos.set(initialLightLoc, 1.0f);
		elapsedTime = System.currentTimeMillis() - prevTime;
		prevTime = System.currentTimeMillis();
		amt += elapsedTime * 0.03f;
		//currentLightPos.rotateAxis((float)Math.toRadians(amt), 0.0f, 0.0f, 1.0f);

		// Set the light position to the light cube position
		currentLightPos.set(lightCubePos.x, lightCubePos.y, lightCubePos.z);
		
		installLights(gl);

		// Skybox ==========================================================================
		gl.glUseProgram(skyRenderProgram);
		mvLoc = gl.glGetUniformLocation(skyRenderProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(skyRenderProgram, "p_matrix");

		mMat.identity().setTranslation(cameraLoc.x(), cameraLoc.y(), cameraLoc.z());

		// Set up the Model View matrix
		gl.glUseProgram(skyRenderProgram);

		vLoc = gl.glGetUniformLocation(skyRenderProgram, "v_matrix");
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));

		pLoc = gl.glGetUniformLocation(skyRenderProgram, "p_matrix");
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
				
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxTexture);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);	     // cube is CW, but we are viewing the inside
		gl.glDisable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		gl.glEnable(GL_DEPTH_TEST);
		
		// Moon ==========================================================================
		gl.glUseProgram(renderingProgram);
		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");

		setMaterialProperties(gl, moonAmb, moonDif, moonSpe, moonShi);

		mMat.identity();
		mMat.translation(3.0f, 0.8f, 0.0f);
		mMat.scale(1.0f, 1.0f, 1.0f);
		mMat.rotate((float)Math.toRadians(amt), 0.0f, 1.0f, 0.0f);

		// Update matrices
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		invTrMat.identity();
		mvMat.invert(invTrMat);
		invTrMat.transpose();

		// Generate the object
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// Texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, moonTexture);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

		// Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);

		// UFO ================================================================================
		gl.glUseProgram(renderingProgram);
		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");

		setMaterialProperties(gl, silvAmb, silvDif, silvSpe, silvShi);

		// Make a translation matrix
		mvStack.pushMatrix();
		mvStack.translate(3.0f, 1.0f, 0.0f);
		// Scale matrix
		mvStack.pushMatrix();
		mvStack.scale(0.2f, 0.2f, 0.2f);

		// Update matrices
		mvMat.identity();
		mvMat.mul(vMat);
		invTrMat.identity();
		mvMat.invert(invTrMat);
		invTrMat.transpose();

		// Generate the object
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// Texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, ufoTexture);

		// Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, ufoModel.getNumVertices());

		// Keep the position of the UFO
		mvStack.popMatrix();

		// Borg Cube ==========================================================================
		gl.glUseProgram(renderingProgram);
		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");

		setMaterialProperties(gl, goldAmb, goldDif, goldSpe, goldShi);

		// Translate
		mvStack.pushMatrix();
		mvStack.translate((float)Math.sin(amt/50)*2.0f, (float)Math.cos(amt/50)*2.0f + 2.0f, (float)Math.cos(amt/50)*2.0f);
		// Scale
		mvStack.pushMatrix();
		mvStack.scale(0.3f, 0.3f, 0.3f);
		// Rotate
		mvStack.pushMatrix();
		mvStack.rotate((float)Math.toRadians(amt), 0.0f, -1.0f, 0.0f);

		// Update mMat to include the rotation
		// Note: When using a model-view matrix stack, keep the local matrix updated as well.
		// This issue took me >20 hours to figure out.
		mMat.identity();
		mMat.rotate((float)Math.toRadians(amt), 0.0f, -1.0f, 0.0f);

		// Update matrices
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		invTrMat.identity();
		mvMat.invert(invTrMat);
		invTrMat.transpose();

		// Generate the object
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// Texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, borgCubeTexture);

		// Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);
		
		gl.glFrontFace(GL_CW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);

		// Pop the stack for every push
		mvStack.popMatrix(); mvStack.popMatrix(); mvStack.popMatrix();

		// Light cube =======================================================================
		gl.glUseProgram(renderingProgram);
		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");

		setMaterialProperties(gl, whiteAmb, whiteDif, whiteSpe, whiteShi);

		mMat.identity();
		mMat.translation(lightCubePos.x(), lightCubePos.y(), lightCubePos.z());
		mMat.scale(0.1f, 0.1f, 0.1f);

		// Update matrices
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		invTrMat.identity();
		mvMat.invert(invTrMat);
		invTrMat.transpose();

		// Generate the object
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// Texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, lightCubeTexture);

		// Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		//gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, ufoModel.getNumVertices());

		// Axes ===============================================================================
		if (axesEnabled == true) {
			gl.glUseProgram(axesRenderProgram);
			mvLoc = gl.glGetUniformLocation(axesRenderProgram, "mv_matrix");
			pLoc = gl.glGetUniformLocation(axesRenderProgram, "p_matrix");

			mMatAxes.identity();
			mvMatAxes.identity();
			mvMatAxes.mul(vMat);
			mvMatAxes.mul(mMatAxes);

			gl.glUniformMatrix4fv(mvLoc, 1, false, mvMatAxes.get(vals));
			gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
			gl.glDrawArrays(GL_LINES, 0, 6);
		}

		mvStack.clear();
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram = Utils.createShaderProgram("a3/vertShader.glsl", "a3/fragShader.glsl");
		skyRenderProgram = Utils.createShaderProgram("a3/skyVertShader.glsl", "a3/skyFragShader.glsl");
		axesRenderProgram = Utils.createShaderProgram("a3/axesVertShader.glsl", "a3/axesFragShader.glsl");

		borgCubeTexture = Utils.loadTexture("a3/BorgCubeTexture.png");
		ufoTexture = Utils.loadTexture("a3/UFOTexture.jpg");
		lightCubeTexture = Utils.loadTexture("a3/LightCubeTexture.jpg");
		moonTexture = Utils.loadTexture("a3/moon.jpg");

		ufoModel = new ImportedModel("a3/UFO.obj");

		prevTime = System.currentTimeMillis();

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		setupVertices();
		
		skyboxTexture = Utils.loadCubeMap("a3/cubeMap");
		// Enable mipmapping
		gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
	}
	
	private void installLights(GL4 gl) {
		// The light position needs to be changed to the camera's coordinate system
		lightWorldPos.set(lightCubePos.x, lightCubePos.y, lightCubePos.z, 1.0f);
		mvMat.transform(lightWorldPos, lightVPos);

    	lightPos[0] = lightVPos.x();
    	lightPos[1] = lightVPos.y();
    	lightPos[2] = lightVPos.z();

		// get the locations of the light and material fields in the shader
		globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
		ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
		diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
		specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
		posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");
	
		// Enable the global light
		gl.glProgramUniform4fv(renderingProgram, globalAmbLoc, 1, globalAmbient, 0);

		// If the positional light is on (determined by isOn), render it
		if (isOn) {
			gl.glProgramUniform4fv(renderingProgram, ambLoc, 1, lightAmbient, 0);
			gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, lightDiffuse, 0);
			gl.glProgramUniform4fv(renderingProgram, specLoc, 1, lightSpecular, 0);
			gl.glProgramUniform3fv(renderingProgram, posLoc, 1, lightPos, 0);
		} else {
			gl.glProgramUniform4fv(renderingProgram, ambLoc, 1, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);
			gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);
			gl.glProgramUniform4fv(renderingProgram, specLoc, 1, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);
			gl.glProgramUniform3fv(renderingProgram, posLoc, 1, new float[] {0.0f, 0.0f, 0.0f}, 0);
		}
	}

	private void setMaterialProperties(GL4 gl, float[] amb, float[] dif, float[] spe, float shi) {
		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");

		gl.glProgramUniform4fv(renderingProgram, mambLoc, 1, amb, 0);
		gl.glProgramUniform4fv(renderingProgram, mdiffLoc, 1, dif, 0);
		gl.glProgramUniform4fv(renderingProgram, mspecLoc, 1, spe, 0);
		gl.glProgramUniform1f(renderingProgram, mshiLoc, shi);

	}

	private void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
	
		// Skybox = 0-1 ================================================================================
		float[] skyVertexPositions =
		{	-1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
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
		
		float[] skyTextureCoords =
		{	1.00f, 0.6666666f, 1.00f, 0.3333333f, 0.75f, 0.3333333f,	// back face lower right
			0.75f, 0.3333333f, 0.75f, 0.6666666f, 1.00f, 0.6666666f,	// back face upper left
			0.75f, 0.3333333f, 0.50f, 0.3333333f, 0.75f, 0.6666666f,	// right face lower right
			0.50f, 0.3333333f, 0.50f, 0.6666666f, 0.75f, 0.6666666f,	// right face upper left
			0.50f, 0.3333333f, 0.25f, 0.3333333f, 0.50f, 0.6666666f,	// front face lower right
			0.25f, 0.3333333f, 0.25f, 0.6666666f, 0.50f, 0.6666666f,	// front face upper left
			0.25f, 0.3333333f, 0.00f, 0.3333333f, 0.25f, 0.6666666f,	// left face lower right
			0.00f, 0.3333333f, 0.00f, 0.6666666f, 0.25f, 0.6666666f,	// left face upper left
			0.25f, 0.3333333f, 0.50f, 0.3333333f, 0.50f, 0.0000000f,	// bottom face upper right
			0.50f, 0.0000000f, 0.25f, 0.0000000f, 0.25f, 0.3333333f,	// bottom face lower left
			0.25f, 1.0000000f, 0.50f, 1.0000000f, 0.50f, 0.6666666f,	// top face upper right
			0.50f, 0.6666666f, 0.25f, 0.6666666f, 0.25f, 1.0000000f		// top face lower left
		};

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		// Vertex coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer cvertBuf = Buffers.newDirectFloatBuffer(skyVertexPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, cvertBuf.limit()*4, cvertBuf, GL_STATIC_DRAW);

		// Texture coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer ctexBuf = Buffers.newDirectFloatBuffer(skyTextureCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, ctexBuf.limit()*4, ctexBuf, GL_STATIC_DRAW);

		// UFO = 2-4 =================================================================================
		Vector3f[] ufoPositions = ufoModel.getVertices();
		Vector2f[] ufoTextureCoords = ufoModel.getTexCoords();
		Vector3f[] ufoNormalCoords = ufoModel.getNormals();

		float[] ufoPvalues = new float[ufoPositions.length*3];
		float[] ufoTvalues = new float[ufoTextureCoords.length*2];
		float[] ufoNvalues = new float[ufoNormalCoords.length*3];

		for (int i=0; i< ufoPositions.length; i++)
		{	ufoPvalues[i*3]   = (float) ufoPositions[i].x();
			ufoPvalues[i*3+1] = (float) ufoPositions[i].y();
			ufoPvalues[i*3+2] = (float) ufoPositions[i].z();
			ufoTvalues[i*2]   = (float) ufoTextureCoords[i].x();
			ufoTvalues[i*2+1] = (float) ufoTextureCoords[i].y();
			ufoNvalues[i*3]   = (float) ufoNormalCoords[i].x();
			ufoNvalues[i*3+1] = (float) ufoNormalCoords[i].y();
			ufoNvalues[i*3+2] = (float) ufoNormalCoords[i].z();
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(ufoPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(ufoTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(ufoNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4, norBuf, GL_STATIC_DRAW);

		// Borg Cube 5-7 ===========================================================================
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
			1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			// Top face
			0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f
		};

		float[] cubeNormalCoords = {
			// Back face
			0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
			// Right face
			1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 
			// Front face
			0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 
			// Left face
			-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
			// Bottom face
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
			// Top face
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
		};

		// Vertex coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(cubePositions);
		gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit()*4, cubeBuf, GL_STATIC_DRAW);

		// Texture coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer cTexBuf = Buffers.newDirectFloatBuffer(cubeTextureCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, cTexBuf.limit()*4, cTexBuf, GL_STATIC_DRAW);

		// Normal coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer cNorBuf = Buffers.newDirectFloatBuffer(cubeNormalCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, cNorBuf.limit()*4, cNorBuf, GL_STATIC_DRAW);

		// Light cube = 8-11 ========================================================================
		float[] lightCubePositions = {
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

		float[] lightCubeTextureCoords = {
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
			1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			// Top face
			0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f
		};

		float[] lightCubeNormalCoords = {
			// Back face
			0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
			// Right face
			1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 
			// Front face
			0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 
			// Left face
			-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
			// Bottom face
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
			// Top face
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
		};

		// Vertex coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		FloatBuffer lightCubeBuf = Buffers.newDirectFloatBuffer(lightCubePositions);
		gl.glBufferData(GL_ARRAY_BUFFER, lightCubeBuf.limit()*4, lightCubeBuf, GL_STATIC_DRAW);

		// Texture coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		FloatBuffer lightCTexBuf = Buffers.newDirectFloatBuffer(lightCubeTextureCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, lightCTexBuf.limit()*4, lightCTexBuf, GL_STATIC_DRAW);

		// Normal coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		FloatBuffer lightCNorBuf = Buffers.newDirectFloatBuffer(lightCubeNormalCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, lightCNorBuf.limit()*4, lightCNorBuf, GL_STATIC_DRAW);

		// Moon 11-13 ===========================================================================
		mySphere = new Sphere(96);
		numSphereVerts = mySphere.getIndices().length;
	
		int[] indices = mySphere.getIndices();
		Vector3f[] vert = mySphere.getVertices();
		Vector2f[] tex  = mySphere.getTexCoords();
		Vector3f[] norm = mySphere.getNormals();
		
		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];
		float[] nvalues = new float[indices.length*3];
		
		for (int i=0; i<indices.length; i++)
		{	pvalues[i*3] = (float) (vert[indices[i]]).x;
			pvalues[i*3+1] = (float) (vert[indices[i]]).y;
			pvalues[i*3+2] = (float) (vert[indices[i]]).z;
			tvalues[i*2] = (float) (tex[indices[i]]).x;
			tvalues[i*2+1] = (float) (tex[indices[i]]).y;
			nvalues[i*3] = (float) (norm[indices[i]]).x;
			nvalues[i*3+1]= (float)(norm[indices[i]]).y;
			nvalues[i*3+2]=(float) (norm[indices[i]]).z;
		}
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4,norBuf, GL_STATIC_DRAW);
	}

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
			camera.moveCamera(cameraN, 0.5f);
			break;

			case KeyEvent.VK_S:
			camera.moveCamera(cameraN, -0.5f);
			break;

			case KeyEvent.VK_A:
			camera.moveCamera(cameraU, -0.5f);
			break;

			case KeyEvent.VK_D:
			camera.moveCamera(cameraU, 0.5f);
			break;

			case KeyEvent.VK_Q:
			camera.moveCamera(cameraV, 0.5f);
			break;

			case KeyEvent.VK_E:
			camera.moveCamera(cameraV, -0.5f);
			break;

			case KeyEvent.VK_LEFT:
			camera.rotateCamera(0.5f, cameraV);
			break;

			case KeyEvent.VK_RIGHT:
			camera.rotateCamera(-0.5f, cameraV);
			break;

			case KeyEvent.VK_UP:
			camera.rotateCamera(0.5f, cameraU);
			break;

			case KeyEvent.VK_DOWN:
			camera.rotateCamera(-0.5f, cameraU);
			break;

			// Light cube movement
			case KeyEvent.VK_J:
			lightCubePos.x -= 0.2f;
			break;

			case KeyEvent.VK_L:
			lightCubePos.x += 0.2f;
			break;

			case KeyEvent.VK_I:
			lightCubePos.z += 0.2f;
			break;

			case KeyEvent.VK_K:
			lightCubePos.z -= 0.2f;
			break;

			case KeyEvent.VK_U:
			lightCubePos.y += 0.2f;
			break;

			case KeyEvent.VK_O:
			lightCubePos.y -= 0.2f;
			break;

			case KeyEvent.VK_Y:
			isOn = !isOn;
			if (isOn) {
				System.out.println("Light is ON");
			} else {
				System.out.println("Light is OFF");
			}
			break;

			case KeyEvent.VK_SPACE:
			axesEnabled = !axesEnabled;
			break;
		}
	}


	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	public static void main(String[] args) { new Code(); }
	public void dispose(GLAutoDrawable drawable) {}
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{	aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
	}
}