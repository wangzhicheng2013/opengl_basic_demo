package com.example.openglesbasicdemo;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
public class BasicTools {
    private final static String TAG = BasicTools.class.getSimpleName();

    private class GLShader {
        public int program;
        public int vertexShader;
        public int fragmentShader;
    }

    private String VertexShaderGraph =
            "attribute vec4 aPosition;\n" +
                    "attribute vec4 aColor;\n" +
                    "uniform float vPointSize;\n" +
                    "varying vec4 vFragColor;\n" +
                    "void main(){\n" +
                    "   gl_PointSize = vPointSize;\n" +
                    "   gl_Position = aPosition;\n" +
                    "   vFragColor = aColor;\n" +
                    "}\n";

    private String FragmentShaderGraph =
            "precision mediump float;\n" +
                    "varying vec4 vFragColor;\n" +
                    "void main(){\n" +
                    "   gl_FragColor=vFragColor;\n" +
                    "}\n";

    private int createProgram(GLShader shader, String pVertexSource, String pFragmentSource) {
        if (shader.program != 0) {
            GLES30.glUseProgram(shader.program);
            return shader.program;
        }
        shader.vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, pVertexSource);
        shader.fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, pFragmentSource);
        int program = GLES30.glCreateProgram();
        if (0 == program) {
            Log.e(TAG, "glCreateProgram error!");
            return 0;
        }
        GLES30.glAttachShader(program, shader.vertexShader);
        GLES30.glAttachShader(program, shader.fragmentShader);
        GLES30.glLinkProgram(program);
        int linkStatus[] = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, IntBuffer.wrap(linkStatus));
        if (GLES30.GL_FALSE == linkStatus[0]) {
            Log.e(TAG, "Error linking program:");
            return 0;
        }
        GLES30.glValidateProgram(program);
        int validateStatus[] = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_VALIDATE_STATUS, IntBuffer.wrap(validateStatus));
        if (GLES30.GL_FALSE == validateStatus[0]) {
            Log.e(TAG, "Error validate program:");
            return 0;
        }
        shader.program = program;
        GLES30.glUseProgram(shader.program);
        return shader.program;
    }

    private int loadShader(int shaderType, String pSource) {
        int shader = GLES30.glCreateShader(shaderType);
        GLES30.glShaderSource(shader, pSource);
        GLES30.glCompileShader(shader);

        String infoLog = null;
        if (GLES30.glIsShader(shader)) {
            infoLog = GLES30.glGetShaderInfoLog(shader);
        } else {
            infoLog = GLES30.glGetProgramInfoLog(shader);
        }
        if ((infoLog != null) && (infoLog.length() > 0)) {
            Log.e(TAG, infoLog);
        }

        int compiled[] = new int[1];
        compiled[0] = GLES30.GL_FALSE;
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, IntBuffer.wrap(compiled));
        if (GLES30.GL_FALSE == compiled[0]) {
            Log.e(TAG, "compile error! " + pSource);
        }
        return shader;
    }

    private void releaseShader(GLShader shader) {
        if (shader.vertexShader != 0) {
            GLES30.glDeleteShader(shader.vertexShader);
            shader.vertexShader = 0;
        }

        if (shader.fragmentShader != 0) {
            GLES30.glDeleteShader(shader.fragmentShader);
            shader.fragmentShader = 0;
        }

        if (shader.program != 0) {
            GLES30.glDeleteProgram(shader.program);
            shader.program = 0;
        }
    }

    private FloatBuffer getFloatBuffer(float[] a) {
        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
        mbb.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = mbb.asFloatBuffer();
        floatBuffer.put(a);
        floatBuffer.position(0);
        return floatBuffer;
    }

    private GLShader GLShaderGraph = new GLShader();

    public void drawRect(float left, float top, float right, float bottom, float red, float green, float blue, float alpha, float lineWidth) {
        float[] ver = {
                left, top,
                left, bottom,
                right, bottom,
                right, top
        };

        float[] col = {
                red, green, blue, alpha,
                red, green, blue, alpha,
                red, green, blue, alpha,
                red, green, blue, alpha};

        FloatBuffer posBuf = getFloatBuffer(ver);
        FloatBuffer colBuf = getFloatBuffer(col);

        int programid = createProgram(GLShaderGraph, VertexShaderGraph, FragmentShaderGraph);
        int aGraphPositionLocation = GLES30.glGetAttribLocation(programid, "aPosition");
        int aGraphColorLocation = GLES30.glGetAttribLocation(programid, "aColor");

        GLES30.glLineWidth(lineWidth);
        GLES30.glEnableVertexAttribArray(aGraphPositionLocation);
        GLES30.glEnableVertexAttribArray(aGraphColorLocation);
        GLES30.glVertexAttribPointer(aGraphPositionLocation, 2, GLES30.GL_FLOAT, false, 0, posBuf);
        GLES30.glVertexAttribPointer(aGraphColorLocation, 4, GLES30.GL_FLOAT, false, 0, colBuf);

        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, 4);

        GLES30.glDisableVertexAttribArray(aGraphPositionLocation);
        GLES30.glDisableVertexAttribArray(aGraphColorLocation);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float red, float green, float blue, float alpha, float lineWidth) {
        float[] ver = {
                x1, y1,
                x2, y2
        };

        float[] col = {red, green, blue, alpha,
                red, green, blue, alpha};

        FloatBuffer posBuf = getFloatBuffer(ver);
        FloatBuffer colBuf = getFloatBuffer(col);

        int programid = createProgram(GLShaderGraph, VertexShaderGraph, FragmentShaderGraph);
        int aGraphPositionLocation = GLES30.glGetAttribLocation(programid, "aPosition");
        int aGraphColorLocation = GLES30.glGetAttribLocation(programid, "aColor");

        GLES30.glLineWidth(lineWidth);
        GLES30.glEnableVertexAttribArray(aGraphPositionLocation);
        GLES30.glEnableVertexAttribArray(aGraphColorLocation);
        GLES30.glVertexAttribPointer(aGraphPositionLocation, 2, GLES30.GL_FLOAT, false, 0, posBuf);
        GLES30.glVertexAttribPointer(aGraphColorLocation, 4, GLES30.GL_FLOAT, false, 0, colBuf);

        GLES30.glDrawArrays(GLES30.GL_LINES, 0, 2);

        GLES30.glDisableVertexAttribArray(aGraphPositionLocation);
        GLES30.glDisableVertexAttribArray(aGraphColorLocation);
    }

    public void drawPoint(float x1, float y1, float red, float green, float blue, float alpha, float pointSize) {
        float[] ver = {
                x1, y1
        };

        float[] col = {red, green, blue, alpha};

        FloatBuffer posBuf = getFloatBuffer(ver);
        FloatBuffer colBuf = getFloatBuffer(col);

        int programid = createProgram(GLShaderGraph, VertexShaderGraph, FragmentShaderGraph);
        int aGraphPositionLocation = GLES30.glGetAttribLocation(programid, "aPosition");
        int aGraphColorLocation = GLES30.glGetAttribLocation(programid, "aColor");
        int uPointSizeLocation = GLES30.glGetUniformLocation(programid, "vPointSize");  // 点大小句柄

        GLES30.glEnableVertexAttribArray(aGraphPositionLocation);
        GLES30.glEnableVertexAttribArray(aGraphColorLocation);
        GLES30.glEnableVertexAttribArray(uPointSizeLocation);
        GLES30.glVertexAttribPointer(aGraphPositionLocation, 2, GLES30.GL_FLOAT, false, 0, posBuf);
        GLES30.glVertexAttribPointer(aGraphColorLocation, 4, GLES30.GL_FLOAT, false, 0, colBuf);
        GLES30.glUniform1f(uPointSizeLocation, pointSize);

        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1);

        GLES30.glDisableVertexAttribArray(aGraphPositionLocation);
        GLES30.glDisableVertexAttribArray(aGraphColorLocation);
    }

    public void uninit() {
        releaseShader(GLShaderGraph);
    }

    /**
     * 将图像坐标系的像素坐标转换到OPENGL坐标系的xy坐标(-1,1)
     *
     * @param x
     * @param width
     * @return
     */
    public static float convertX2Opengl(int x, int width) {
        return ((float) x - (float) width / 2.0f) / ((float) width / 2.0f);
    }

    public static float convertY2Opengl(int y, int height) {
        return ((float) height / 2.0f - (float) y) / ((float) height / 2.0f);

    }
}
