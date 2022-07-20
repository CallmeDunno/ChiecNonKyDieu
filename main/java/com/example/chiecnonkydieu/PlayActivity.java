package com.example.chiecnonkydieu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    ImageView imageViewCircle, imageViewArrow, imageViewAdd, imageViewGift;
    TextView textViewScore, textViewNotification, textViewNumberTurn;
    Button buttonClick;

    SharedPreferences sharedPreferences;

    //khởi tạo giá trị của vòng quay, vì vòng quay sẽ quay theo chiều kim đồng hồ nên giá trị sẽ lấy ngược lại
    static int []itemCircle = {2000, 100, 500, 1, 200, 700,
            1000, 400, 2, 900, 3, 300, 800, 4, 1000, 400, 600,
            300, 5, 200, 900, 700, 6, 300};
    //Khởi tạo góc quay, có 24 ô tương ứng với mỗi ô là 15 độ
    static int []angle = {0, 15, 30, 45, 60, 75, 90, 105,
            120, 135, 150, 165, 180, 195,
            210, 225, 240, 255, 270, 285,
            300, 315, 330, 345};
    //giá trị bắt đầu
    int from;


    private void mapping(){
        imageViewArrow = findViewById(R.id.imageViewArrow);
        imageViewCircle = findViewById(R.id.imageViewCircle);
        textViewNotification = findViewById(R.id.textViewNotification);
        textViewScore  =findViewById(R.id.textViewScore);
        buttonClick = findViewById(R.id.buttonClick);
        imageViewAdd = findViewById(R.id.imageViewAdd);
        textViewNumberTurn = findViewById(R.id.textViewNumberTurn);
        imageViewGift = findViewById(R.id.imageViewGift);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mapping();

        //lưu lượt và điểm
        sharedPreferences = getSharedPreferences("DataScore", MODE_PRIVATE);
        textViewScore.setText(sharedPreferences.getString("score", ""));
        textViewNumberTurn.setText(sharedPreferences.getString("turn", ""));
        from = sharedPreferences.getInt("fromDeg", 0);

        //nếu khi khởi tạo có lượt bằng 0 thì sẽ không quay được
        if(textViewNumberTurn.getText().toString().equals("0")){
            buttonClick.setEnabled(false);
        }

        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBuyTurn();
            }
        });

        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //khi click thì sẽ tạm thời Disable button đi tránh việc click liên tục
                buttonClick.setEnabled(false);

                //khi click sẽ trừ đi 1 lượt quay
                int turn = Integer.parseInt(textViewNumberTurn.getText().toString()) - 1;
                textViewNumberTurn.setText(String.valueOf(turn));

                //tạo góc quay ngẫu nhiên
                Random random = new Random();
                int position = random.nextInt(angle.length);
                int to = angle[position];
                //tạo thành 1 vòng quay liên tục
                if(from >= 360){
                    from -= 360;
                }

                //hiệu ứng quay của image
                RotateAnimation rotateAnimation = new RotateAnimation(from, to + (360*5), Animation.RELATIVE_TO_SELF,
                                                                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(500);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        textViewNotification.setText(null);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        int i = Integer.parseInt(textViewScore.getText().toString());
                        switch (itemCircle[position]) {
                            case 1:
                                textViewNotification.setText(R.string.text_1);
                                i = i*2;
                                textViewScore.setText(String.valueOf(i));
                                break;
                            case 2:
                                textViewNotification.setText(R.string.text_2);
                                if(turn > 0){
                                    textViewNumberTurn.setText(String.valueOf(turn-1));
                                }

                                break;
                            case 3:
                                textViewNotification.setText(R.string.text_3);
                                imageViewGift.setVisibility(View.VISIBLE);
                                int finalI = i;
                                imageViewGift.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        textViewNotification.setText(R.string.text_3_noti);
                                        textViewScore.setText(String.valueOf(finalI + 2000));
                                        imageViewGift.setVisibility(View.INVISIBLE);
                                    }
                                });
                                break;
                            case 4:
                                textViewNotification.setText(R.string.text_4);
                                textViewScore.setText("0");
                                break;
                            case 5:
                                textViewNotification.setText(R.string.text_5);
                                textViewNumberTurn.setText(String.valueOf(turn+1));
                                break;
                            case 6:
                                textViewNotification.setText(R.string.text_6);
                                i = i / 2;
                                textViewScore.setText(String.valueOf(i));
                                break;
                            default:
                                textViewNotification.setText(String.valueOf(itemCircle[position]));
                                i += itemCircle[position];
                                textViewScore.setText(String.valueOf(i));
                                break;
                        }
                        from += to;

                        //lưu dữ liệu mỗi khi quay xong
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("score", textViewScore.getText().toString());
                        editor.putString("turn", textViewNumberTurn.getText().toString());
                        editor.putInt("fromDeg", from);
                        editor.apply();

                        //sau khi quay xong, nếu lượt = 0 thì ẩn button
                        int numberTurn = Integer.parseInt(textViewNumberTurn.getText().toString());
                        buttonClick.setEnabled(numberTurn != 0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageViewCircle.startAnimation(rotateAnimation);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.homeMain:
                startActivity(new Intent(PlayActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_intent_ltc, R.anim.anim_intent_ctr);
                break;
            case R.id.tutorial:
                DialogTutorial();
                break;
            case R.id.contactDialog:
                DialogContact();
                break;
            case R.id.helpDialog:
                DialogHelp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogTutorial(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Tutorial");
        String str = "Bấm vào quay để quay vòng quay, nếu không đủ điểm để mua lượt thì bấm vào help";
        dialog.setMessage(str);
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void DialogHelp(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Help");
        String str = "Nếu hết điểm thì bấm vào đây!";
        dialog.setMessage(str);
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textViewScore.setText(String.valueOf(5000));
                textViewNumberTurn.setText(String.valueOf(1));
                textViewNotification.setText("Bạn nhận được 5000 điểm và 1 lượt quay");
                buttonClick.setEnabled(true);
            }
        });
        dialog.show();
    }

    private void DialogContact(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Contact");
        String str = "github.com/CallmeDunno";
        dialog.setMessage(str);
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void DialogBuyTurn(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Mua lượt");
        alertDialog.setMessage("1 lượt = 1000 điểm. Bạn có muốn mua không?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int score = Integer.parseInt(textViewScore.getText().toString());
                if(score < 1000){
                    DialogDontBuy();
                } else {
                    score -= 1000;
                    textViewScore.setText(String.valueOf(score));
                    int turn = Integer.parseInt(textViewNumberTurn.getText().toString()) + 1;
                    textViewNumberTurn.setText(String.valueOf(turn));
                    buttonClick.setEnabled(true);
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    private void DialogDontBuy(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Lỗi!!!");
        dialog.setMessage("Bạn không đủ điểm để mua");
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }
}