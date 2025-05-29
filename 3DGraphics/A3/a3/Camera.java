package a3;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    // Rotation
	private Vector3f cameraU = new Vector3f();
	private Vector3f cameraV = new Vector3f();
	private Vector3f cameraN = new Vector3f();

    // Position
	private Vector3f cameraLoc = new Vector3f(0.0f, 0.0f, 0.0f);

    // Matrixes
	private Matrix4f tMatrix = new Matrix4f(); // A translation matrix for movement
    private Matrix4f rMatrix = new Matrix4f(); // A rotation matrix for orientation
    private Matrix4f vMat = new Matrix4f(); // The view matrix

    public Camera(Vector3f cameraLoc) {
		cameraU.set(1.0f, 0.0f, 0.0f);
		cameraV.set(0.0f, 1.0f, 0.0f);
		cameraN.set(0.0f, 0.0f, -1.0f);

        this.cameraLoc = cameraLoc;
        updateCamera();
    }

	public void updateCamera() {
		tMatrix = new Matrix4f();
		// setTranslation makes a translation matrix as described in the Appendix
		tMatrix.setTranslation(-cameraLoc.x(), -cameraLoc.y(), -cameraLoc.z());

		// Set the rmatrix to the camera's orientation in the left-handed orientation
		rMatrix.identity();
		rMatrix = new Matrix4f().set(
		cameraU.x, cameraV.x, -cameraN.x, 0.0f,
		cameraU.y, cameraV.y, -cameraN.y, 0.0f,
		cameraU.z, cameraV.z, -cameraN.z, 0.0f,
		0.0f, 0.0f,  0.0f, 1.0f
		); // You basically have to invert the matrix when manually coding it like this.

		vMat.identity().mul(rMatrix).mul(tMatrix);
	}

	public void moveCamera(Vector3f cameraVector, float toMove) {
		// Forward C’ = C + (a*N)
		// Backward C’ = C - (a*N)
		// Right C’ = C + (a*U)
		// Left C - (a*U)
		// Up C’ = C + (a*V)
		// Down C’ = C - (a*V)
		cameraLoc.x += toMove * cameraVector.x;
		cameraLoc.y += toMove * cameraVector.y;
		cameraLoc.z += toMove * cameraVector.z;

        updateCamera();
	}

	public void rotateCamera(float angle, Vector3f axis) {
		// Make the rotator matrix into an identity matrix
		rMatrix.identity();
		rMatrix.rotate((float) Math.toRadians(angle), axis);

		rMatrix.transformDirection(cameraU);
		rMatrix.transformDirection(cameraV);
		rMatrix.transformDirection(cameraN);

		cameraU.normalize();
		cameraV.normalize();
		cameraN.normalize();

        updateCamera();
	}

    public Vector3f getCameraU() {
        return cameraU;
    }

    public void setCameraU(Vector3f cameraU) {
        this.cameraU = cameraU;
    }

    public Vector3f getCameraV() {
        return cameraV;
    }

    public void setCameraV(Vector3f cameraV) {
        this.cameraV = cameraV;
    }

    public Vector3f getCameraN() {
        return cameraN;
    }

    public void setCameraN(Vector3f cameraN) {
        this.cameraN = cameraN;
    }

    public Vector3f getCameraLoc() {
        return cameraLoc;
    }

    public void setCameraLoc(Vector3f cameraLoc) {
        this.cameraLoc = cameraLoc;
    }

    public Matrix4f gettMatrix() {
        return tMatrix;
    }

    public void settMatrix(Matrix4f tMatrix) {
        this.tMatrix = tMatrix;
    }

    public Matrix4f getrMatrix() {
        return rMatrix;
    }

    public void setrMatrix(Matrix4f rMatrix) {
        this.rMatrix = rMatrix;
    }

    public Matrix4f getvMat() {
        return vMat;
    }

    public void setvMat(Matrix4f vMat) {
        this.vMat = vMat;
    }

}