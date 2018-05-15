package pl.kolis.mobilevoter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newSessionBtn = (Button) findViewById(R.id.new_session_btn);
        newSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSessionDialog();
            }
        });

    }

    private void showSessionDialog() {
        String[] items = getResources().getStringArray(R.array.session_array);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick session type");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent intent = new Intent(MainActivity.this, OpenSessionActivity.class);
                    startActivity(intent);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}

