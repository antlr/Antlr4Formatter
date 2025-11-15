package com.example.anter4codeformat;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.anter4codeformat.databinding.ActivityMainBinding;
import com.khubla.antlr4formatter.Antlr4Formatter;
import com.khubla.antlr4formatter.Antlr4FormatterException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  private ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inflate and get instance of binding
    binding = ActivityMainBinding.inflate(getLayoutInflater());

    // set content view to binding's root
    setContentView(binding.getRoot());
    binding.btnFormat.setOnClickListener(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.binding = null;
  }

  public String g4format(String code) throws Antlr4FormatterException {
    return Antlr4Formatter.format(code);
  }

  @Override
  public void onClick(View arg0) {
    // TODO: Implement this method
    try {
      binding.format.setText(g4format(binding.format.getText().toString()));
    } catch (Antlr4FormatterException err) {
      binding.format.setText(err.getLocalizedMessage());
    }
  }
}
