package com.yiny.lifegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class ChessboardView extends View implements View.OnTouchListener {
    String TAG = "Chessboard";

    Paint paint;
    int startX, startY,eWidth,eHeight;
    int[][] matrix;

    int curLiveNum = 0;

    int line = 15;
    int width = 900;
    int height = 900;

    public ChessboardView(Context context) {
        this(context, null);
    }

    public ChessboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        matrix = new int[line + 1][line + 1];
        for (int i = 0; i <= line; i++) {
            for (int j = 0; j <= line; j++) {
                matrix[i][j] = 0;
            }
        }
        this.setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        if (width >= Math.min(viewWidth, viewHeight)) {
            width = height = Math.min(viewHeight, viewWidth) - 100;
        }
        startX = (viewWidth - width) / 2;
        startY = (viewHeight - height) / 2;
        eWidth=width/line;
        eHeight=width/line;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x77cdb175);
        canvas.drawRect(new RectF(startX, startY, startX + width, startY + height), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.0f);
        paint.setColor(Color.BLACK);
        for (int i = 0; i <= line; i++) {
            canvas.drawLine(startX, startY + eHeight * i, startX + width, startY + eHeight * i, paint);
            canvas.drawLine(startX + eWidth * i, startY, startX + eWidth * i, startY + height, paint);
        }

        paint.setStyle(Paint.Style.FILL);
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[row].length; column++) {
                if (matrix[row][column] == 1) {
                    canvas.drawCircle(startX + eWidth * column, startY + eHeight * row, eWidth / 2 - 5, paint);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getX() >= startX - eWidth/2 && motionEvent.getX() <= startX + width + eWidth/2
                && motionEvent.getY() >= startY - eWidth/2 && motionEvent.getY() <= startY + height + eWidth/2
                && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            int row=(int)(motionEvent.getY()-startY)/eHeight;
            int column=(int)(motionEvent.getX()-startX)/eWidth;
            matrix[row][column]=1;

            invalidate();
        }
        return false;
    }

    public void reset(){
        for(int row=0;row<=line;row++){
            for(int column=0;column<=line;column++){
                matrix[row][column]=0;
            }
        }
        invalidate();
    }

    public void run() {
        int[][] copyMatrix = new int[line + 1][line + 1];

        curLiveNum=0;
        for (int row = 0; row <= line; row++) {
            for (int column = 0; column <= line; column++) {
                if (matrix[row][column] == 1) {
                    curLiveNum++;
                    copyMatrix[row][column] = 1;
                } else {
                    copyMatrix[row][column] = 0;
                }
            }
        }

        if (curLiveNum == 0) return;

        int neighbour;
        for(int row=0;row<=line;row++){
            for(int column=0;column<=line;column++){
                neighbour=0;

                if(row-1>=0){
                    if(column-1>=0&&matrix[row-1][column-1]==1) neighbour++;
                    if(matrix[row-1][column]==1) neighbour++;
                    if(column+1<=line&&matrix[row-1][column+1]==1) neighbour++;
                }

                if(column-1>=0&&matrix[row][column-1]==1) neighbour++;
                if(column+1<=line&&matrix[row][column+1]==1) neighbour++;

                if(row+1<=line){
                    if(column-1>=0&&matrix[row+1][column-1]==1) neighbour++;
                    if(matrix[row+1][column]==1) neighbour++;
                    if(column+1<=line&&matrix[row+1][column+1]==1) neighbour++;
                }

                if(matrix[row][column]==1){
                    if(neighbour!=2&&neighbour!=3) copyMatrix[row][column]=0;
                }else{
                    if(neighbour==3) copyMatrix[row][column]=1;
                }
            }
        }

        matrix=copyMatrix;
        invalidate();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ChessboardView.this.run();
            }
        },1000);
    }
}
