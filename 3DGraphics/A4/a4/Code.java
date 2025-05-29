package a4;

import java.nio.*;
import javax.swing.*;
import java.lang.Math;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_CLAMP_TO_EDGE;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_CW;
import static com.jogamp.opengl.GL.GL_DEPTH_ATTACHMENT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_COMPONENT32;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_FRONT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL.GL_LINEAR_MIPMAP_LINEAR;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_NONE;
import static com.jogamp.opengl.GL.GL_POLYGON_OFFSET_FILL;
import static com.jogamp.opengl.GL.GL_REPEAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE1;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static com.jogamp.opengl.GL2ES2.GL_COMPARE_REF_TO_TEXTURE;
import static com.jogamp.opengl.GL2ES2.GL_DEPTH_COMPONENT;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_COMPARE_FUNC;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_COMPARE_MODE;
import static com.jogamp.opengl.GL2GL3.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.joml.*;

public class Code extends JFrame implements GLEventListener, KeyListener {
	private GLCanvas myCanvas;
	private int renderingProgram1, renderingProgram2, axesRenderProgram, skyRenderProgram, normMapRenderProgram, enviornmentGeometryShaderProgram, heightMapProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[15];

	private int skyboxTexture, borgCubeTexture, ufoTexture, lightCubeTexture, moonTexture, moonNormal, borgHeightMap;
	
	private float amt = 0.0f;
	private double prevTime;
	private double elapsedTime;

	// location of objects and the light
	private Vector3f lightLoc = new Vector3f(-3.8f, 2.2f, 1.1f);
	
	private float[] thisAmb, thisDif, thisSpe, matAmb, matDif, matSpe;
	private float thisShi, matShi;
	
	// shadow stuff
	private int scSizeX, scSizeY;
	private int [] shadowTex = new int[1];
	private int [] shadowBuffer = new int[1];
	private Matrix4f lightVmat = new Matrix4f();
	private Matrix4f lightPmat = new Matrix4f();
	private Matrix4f shadowMVP1 = new Matrix4f();
	private Matrix4f shadowMVP2 = new Matrix4f();
	private Matrix4f b = new Matrix4f();

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f();  // perspective matrix
	private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	//private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mLoc, vLoc, pLoc, nLoc, sLoc, mvLoc;
	private int globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, mambLoc, mdiffLoc, mspecLoc, mshiLoc;
	private float aspect;
	private Vector3f currentLightPos = new Vector3f();
	private float[] lightPos = new float[3];
	private Vector3f origin = new Vector3f(0.0f, 0.0f, 0.0f);
	private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

	// Positional light variables
	private Vector3f initialLightLoc = new Vector3f(8.0f, 2.0f, 2.0f);
	private boolean isOn = true;
	private Vector4f lightWorldPos = new Vector4f();
	private Vector4f lightVPos = new Vector4f();

	// Light cube variables
	private Vector3f lightCubePos = new Vector3f(3.0f, 12.0f, -0.4f);

	// Ufo variables
	private ImportedModel ufoModel;

	// Moon variables
	private Sphere mySphere;
	private int numSphereVerts;

	// Stereoscopy variables
	private float IOD = 0.80f; // 0.80f seems to be a good one for this scene. I do not know why it needs to be so high compared to the professor's
	private float near = 0.01f;
	private float far = 100.0f;
	private boolean isThree = true;

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

	// Axes variables
	private boolean axesEnabled = true;
	private Matrix4f mMatAxes = new Matrix4f();
	private Matrix4f mvMatAxes = new Matrix4f(); // Model-view matrix

	// Camera variables
	//private Vector3f cameraLoc = new Vector3f(0.0f, 0.2f, 6.0f);
	private Vector3f cameraLoc = new Vector3f(3.0f, 5.5f, 16.5f);
	Camera camera = new Camera(cameraLoc);
	private Vector3f cameraU, cameraV, cameraN;
	
	public Code()
	{	setTitle("Chapter8 - program 1");
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

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);

		// Calculate move amts
		elapsedTime = System.currentTimeMillis() - prevTime;
		prevTime = System.currentTimeMillis();
		amt += elapsedTime * 0.03f;
		
		currentLightPos.set(lightCubePos);
		
		lightVmat.identity().setLookAt(currentLightPos, origin, up);	// vector from light to origin
		lightPmat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);
		gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTex[0], 0);
	
		gl.glDrawBuffer(GL_NONE);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_POLYGON_OFFSET_FILL);	//  for reducing
		gl.glPolygonOffset(3.0f, 5.0f);		//  shadow artifacts

		passOne();
		
		gl.glDisable(GL_POLYGON_OFFSET_FILL);	// artifact reduction, continued
		
		gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
	
		gl.glDrawBuffer(GL_FRONT);

		// Prevent the light from moving below 12
		if (lightCubePos.y < 12) {
			lightCubePos.y = 12;
		}

		// Stereoscopy
		gl.glColorMask(true, true, true, true); // all color channels enabled for background color
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0.7f, 0.8f, 0.9f, 1.0f); // the fog color is bluish-grey
		gl.glClear(GL_COLOR_BUFFER_BIT);

		gl.glColorMask(true, false, false, false);
		
		passTwo(-1.0f);

		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glColorMask(false, true, true, false);

		passTwo(1.0f);
		
	}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void passOne()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glUseProgram(renderingProgram1);

		mMat.identity();

		// Moon ==============================================================================
		mMat.identity();
		mMat.translation(3.0f, -5.0f, 0.0f);
		mMat.scale(5.0f, 5.0f, 5.0f);
		mMat.rotate((float)Math.toRadians(amt*0.3), 0.0f, 1.0f, 0.0f);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		sLoc = gl.glGetUniformLocation(renderingProgram1, "shadowMVP");
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));

		// Generate the object
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);

		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);


		// UFO ===================================================================================
		mMat.identity();
		mMat.translate(3.0f, 1.0f, 0.0f);
		mMat.scale(0.2f, 0.2f, 0.2f);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		sLoc = gl.glGetUniformLocation(renderingProgram1, "shadowMVP");
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));

		// Generate the object
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]); 

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, ufoModel.getNumVertices());

		// Keep the position of the UFO

		// Borg Cube =============================================================================

		mMat.identity();
		mMat.translate((float)Math.sin(amt/50)*2.0f + 3.0f, 1.0f, (float)Math.cos(amt/50)*2.0f);
		mMat.scale(0.3f, 0.3f, 0.3f);
		mMat.rotate((float)Math.toRadians(amt), 0.0f, -1.0f, 0.0f);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		sLoc = gl.glGetUniformLocation(renderingProgram1, "shadowMVP");
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));

		// Generate the object
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]); 

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);


		// Do not render the light cube in the first pass so it doesn't block the light
		// Light Cube ============================================================================
		/*mMat.identity();
		mMat.translation(lightCubePos.x(), lightCubePos.y(), lightCubePos.z());
		mMat.scale(0.1f, 0.1f, 0.1f);

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		sLoc = gl.glGetUniformLocation(renderingProgram1, "shadowMVP");
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));

		// Generate the objecty
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, lightCubeTexture);

		// Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, ufoModel.getNumVertices());*/
	}
	
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void passTwo(float leftRight)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		// Camera ==========================================================================
		computePerspectiveMatrix(leftRight);
		vMat.identity().setTranslation(-(cameraLoc.x + leftRight * IOD/2.0f), -cameraLoc.y, -cameraLoc.z);

		mLoc = gl.glGetUniformLocation(normMapRenderProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(normMapRenderProgram, "v_matrix");
		pLoc = gl.glGetUniformLocation(normMapRenderProgram, "p_matrix");

		camera.updateCamera();
		cameraU = camera.getCameraU();
		cameraV = camera.getCameraV();
		cameraN = camera.getCameraN();
		cameraLoc = camera.getCameraLoc();

		camera.setCameraLoc(cameraLoc);
		vMat.set(camera.getvMat());
		currentLightPos.set(lightCubePos);

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



		// Moon ===============================================================================
		// Render the normal mapped Moon
		gl.glUseProgram(normMapRenderProgram);
		
		mLoc = gl.glGetUniformLocation(normMapRenderProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(normMapRenderProgram, "v_matrix");
		pLoc = gl.glGetUniformLocation(normMapRenderProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(normMapRenderProgram, "norm_matrix");
		sLoc = gl.glGetUniformLocation(normMapRenderProgram, "shadowMVP");

		thisAmb = moonAmb;
		thisDif = moonDif;
		thisSpe = moonSpe;
		thisShi = moonShi;

		mMat.identity();
		mMat.translation(3.0f, -5.0f, 0.0f);
		mMat.scale(5.0f, 5.0f, 5.0f);
		mMat.rotate((float)Math.toRadians(amt*0.3), 0.0f, 1.0f, 0.0f);

		// Update matrices
		//invTrMat.identity();
		mMat.invert(invTrMat);
		invTrMat.transpose();

		installLights(normMapRenderProgram);
		shadowMVP2.identity();
		shadowMVP2.mul(b);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);

		// Generate the object
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// Texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		//gl.glActiveTexture(GL_TEXTURE0);
    	//gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);

		// Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		// Tangents
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		gl.glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(3);

		// Apply shadows
		//gl.glActiveTexture(GL_TEXTURE0);
		//gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);

		// Apply texture
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, moonTexture);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

		// Apply normal map
		gl.glActiveTexture(gl.GL_TEXTURE2);
		gl.glBindTexture(gl.GL_TEXTURE_2D, moonNormal);

		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);



		// Render with a slight mirror effect
		gl.glUseProgram(enviornmentGeometryShaderProgram);
		
		mLoc = gl.glGetUniformLocation(enviornmentGeometryShaderProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(enviornmentGeometryShaderProgram, "v_matrix");
		mvLoc = gl.glGetUniformLocation(enviornmentGeometryShaderProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(enviornmentGeometryShaderProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(enviornmentGeometryShaderProgram, "norm_matrix");
		sLoc = gl.glGetUniformLocation(enviornmentGeometryShaderProgram, "shadowMVP");

		// UFO ==================================================================================
		thisAmb = silvAmb;
		thisDif = silvDif;
		thisSpe = silvSpe;
		thisShi = silvShi * 10;

		mMat.identity();
		mMat.translate(3.0f, 1.0f, 0.0f);
		mMat.scale(0.2f, 0.2f, 0.2f);

		// Update matrices
		//invTrMat.identity();
		mMat.invert(invTrMat);
		invTrMat.transpose();

		installLights(enviornmentGeometryShaderProgram);
		shadowMVP2.identity();
		shadowMVP2.mul(b);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);

		// Generate the object
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));

		// Positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// Texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		//gl.glActiveTexture(GL_TEXTURE0);
    	//gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);

		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxTexture);

		// Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, ufoModel.getNumVertices());

		
		// Render normally
		gl.glUseProgram(renderingProgram2);
		
		mLoc = gl.glGetUniformLocation(renderingProgram2, "m_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram2, "v_matrix");
		mvLoc = gl.glGetUniformLocation(renderingProgram2, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram2, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram2, "norm_matrix");
		sLoc = gl.glGetUniformLocation(renderingProgram2, "shadowMVP");

		// Borg Cube ==========================================================================
		thisAmb = goldAmb;
		thisDif = goldDif;
		thisSpe = goldSpe;
		thisShi = goldShi;

		// For the matrix stack items, the mMat needs to be updated and passed for shadows
		mMat.identity();
		mMat.translate((float)Math.sin(amt/50)*2.0f + 3.0f, 1.0f, (float)Math.cos(amt/50)*2.0f);
		mMat.scale(0.3f, 0.3f, 0.3f);
		mMat.rotate((float)Math.toRadians(amt), 0.0f, -1.0f, 0.0f);

		// Update matrices
		//invTrMat.identity();
		mMat.invert(invTrMat);
		invTrMat.transpose();

		installLights(renderingProgram2);
		shadowMVP2.identity();
		shadowMVP2.mul(b);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);

		// Generate the object
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
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

		//gl.glActiveTexture(GL_TEXTURE0);
    	//gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);

		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, borgCubeTexture);

		//gl.glActiveTexture(gl.GL_TEXTURE2);
		//gl.glBindTexture(gl.GL_TEXTURE_2D, borgHeightMap);

		// Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);
		
		gl.glFrontFace(GL_CW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);

		// Light Cube ============================================================================
		thisAmb = whiteAmb;
		thisDif = goldDif;
		thisSpe = goldSpe;
		thisShi = goldShi;

		mMat.identity();
		mMat.translation(lightCubePos.x(), lightCubePos.y(), lightCubePos.z());
		mMat.scale(0.1f, 0.1f, 0.1f);

		// Update matrices
		//invTrMat.identity();
		mMat.invert(invTrMat);
		invTrMat.transpose();

		installLights(renderingProgram2);
		shadowMVP2.identity();
		shadowMVP2.mul(b);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);

		// Generate the object
		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
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

		//gl.glActiveTexture(GL_TEXTURE0);
    	//gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);

		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, lightCubeTexture);

		// Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		//gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, ufoModel.getNumVertices());
	}

	private void computePerspectiveMatrix(float leftRight) {
		float top = (float)Math.tan(1.0472f / 2.0f) * (float)near;
		float bottom = -top;
		float frustumshift = (IOD / 2.0f) * near / far;
		float left = -aspect * top - frustumshift * leftRight;
		float right = aspect * top - frustumshift * leftRight;
		pMat.setFrustum(left, right, bottom, top, near, far);
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram1 = Utils.createShaderProgram("a4/vert1shader.glsl", "a4/frag1shader.glsl");
		renderingProgram2 = Utils.createShaderProgram("a4/vert2shader.glsl", "a4/frag2shader.glsl");
		skyRenderProgram = Utils.createShaderProgram("a4/skyVertShader.glsl", "a4/skyFragShader.glsl");
		axesRenderProgram = Utils.createShaderProgram("a4/axesVertShader.glsl", "a4/axesFragShader.glsl");
		normMapRenderProgram = Utils.createShaderProgram("a4/normMapVertShader.glsl", "a4/normMapFragShader.glsl");
		enviornmentGeometryShaderProgram = Utils.createShaderProgram("a4/enviornmentVertexShader.glsl", "a4/geometryShader.glsl", "a4/enviornmentFragShader.glsl");
		//heightMapProgram = Utils.createShaderProgram("a4/heightVertShader.glsl", "a4/heightFragShader.glsl");
		
		borgCubeTexture = Utils.loadTexture("a4/BorgCubeTexture.png");
		ufoTexture = Utils.loadTexture("a4/UFOTexture.jpg");
		lightCubeTexture = Utils.loadTexture("a4/LightCubeTexture.jpg");
		moonTexture = Utils.loadTexture("a4/moon.jpg");
		//borgHeightMap = Utils.loadTexture("a4/BorgHeightMap.png");

		ufoModel = new ImportedModel("a4/UFO.obj");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		skyboxTexture = Utils.loadCubeMap("a4/cubeMap");
		moonNormal = Utils.loadTexture("a4/MoonNormalMap.jpg");

		setupVertices();
		setupShadowBuffers();
				
		b.set(
			0.5f, 0.0f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.5f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f);
		
		// Enable mipmapping
		gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

		prevTime = System.currentTimeMillis();
		currentLightPos.set(lightCubePos);
	}
	
	private void setupShadowBuffers()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		scSizeX = myCanvas.getWidth();
		scSizeY = myCanvas.getHeight();
	
		gl.glGenFramebuffers(1, shadowBuffer, 0);
	
		gl.glGenTextures(1, shadowTex, 0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32,
						scSizeX, scSizeY, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		
		// may reduce shadow border artifacts
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
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

		// Moon 11-14 ===========================================================================
		mySphere = new Sphere(96);
		numSphereVerts = mySphere.getIndices().length;
	
		int[] indices = mySphere.getIndices();
		Vector3f[] vertices = mySphere.getVertices();
		Vector2f[] texCoords = mySphere.getTexCoords();
		Vector3f[] normals = mySphere.getNormals();
		Vector3f[] tangents = mySphere.getTangents();
		
		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];
		float[] nvalues = new float[indices.length*3];
		float[] tanvalues = new float[indices.length*3];

		for (int i=0; i<indices.length; i++)
		{	pvalues[i*3]   = (float) (vertices[indices[i]]).x();
			pvalues[i*3+1] = (float) (vertices[indices[i]]).y();
			pvalues[i*3+2] = (float) (vertices[indices[i]]).z();
			tvalues[i*2]   = (float) (texCoords[indices[i]]).x();
			tvalues[i*2+1] = (float) (texCoords[indices[i]]).y();
			nvalues[i*3]   = (float) (normals[indices[i]]).x();
			nvalues[i*3+1] = (float) (normals[indices[i]]).y();
			nvalues[i*3+2] = (float) (normals[indices[i]]).z();
			tanvalues[i*3] = (float) (tangents[indices[i]]).x();
			tanvalues[i*3+1] = (float) (tangents[indices[i]]).y();
			tanvalues[i*3+2] = (float) (tangents[indices[i]]).z();
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

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		FloatBuffer tanBuf = Buffers.newDirectFloatBuffer(tanvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, tanBuf.limit()*4, tanBuf, GL_STATIC_DRAW);
	}
	
	private void installLights(int renderingProgram)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		lightPos[0]=currentLightPos.x(); lightPos[1]=currentLightPos.y(); lightPos[2]=currentLightPos.z();
	
		lightPos[0] = lightCubePos.x();
		lightPos[1] = lightCubePos.y();
		lightPos[2] = lightCubePos.z();
		
		// set current material values
		matAmb = thisAmb;
		matDif = thisDif;
		matSpe = thisSpe;
		matShi = thisShi;
		
		// get the locations of the light and material fields in the shader
		globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
		ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
		diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
		specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
		posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");
		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");
	
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

		gl.glProgramUniform3fv(renderingProgram, posLoc, 1, lightPos, 0);
		gl.glProgramUniform4fv(renderingProgram, mambLoc, 1, matAmb, 0);
		gl.glProgramUniform4fv(renderingProgram, mdiffLoc, 1, matDif, 0);
		gl.glProgramUniform4fv(renderingProgram, mspecLoc, 1, matSpe, 0);
		gl.glProgramUniform1f(renderingProgram, mshiLoc, matShi);
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

			case KeyEvent.VK_1:
				isThree = !isThree;
				if (isThree) {
					IOD = 0.80f;
					System.out.println("Stereoscopy ON");
				} else {
					IOD = 0.0f;
					System.out.println("Stereoscopy OFF");
				}
			break;

			case KeyEvent.VK_SPACE:
				axesEnabled = !axesEnabled;
				System.out.println("Axes toggled.");
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
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		setupShadowBuffers();
	}
}