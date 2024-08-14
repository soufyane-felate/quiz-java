package com.example.guess;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int incorrectAnswers = 0;
    private TextView questionTextView;
    private ListView choicesListView;
    private Button nextButton;

    private String[][] questionsAndChoices;
    private String[] questions;
    private String[] correctAnswersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.questionTextView);
        choicesListView = findViewById(R.id.choicesListView);
        nextButton = findViewById(R.id.nextButton);

        questionsAndChoices = Choix.getQuestions();
        questions = Question.getQuestions();
        correctAnswersList = Reponse.getReponses();

        displayQuestion(currentQuestionIndex);

        nextButton.setOnClickListener(view -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length) {
                displayQuestion(currentQuestionIndex);
            } else {
                questionTextView.setText("Quiz terminé ! \nVotre score est : " + correctAnswers + " corrects, " + incorrectAnswers + " incorrects");
                choicesListView.setAdapter(null);
                nextButton.setEnabled(false);
                if (correctAnswers>incorrectAnswers){
                   questionTextView.setText("Félicitations, vous avez réussi le test votre marque : "+correctAnswers);
                   questionTextView.setBackgroundColor(Color.GREEN);
                }else {
                    questionTextView.setText("Désolé, vous n'avez pas réussi le test : "+correctAnswersList);
                    questionTextView.setBackgroundColor(Color.RED);
                }
            }
        });
    }

    private void displayQuestion(int index) {
        questionTextView.setText(questions[index]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questionsAndChoices[index]);
        choicesListView.setAdapter(adapter);

        for (int i = 0; i < choicesListView.getChildCount(); i++) {
            choicesListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        }

        choicesListView.setEnabled(true);

        choicesListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedChoice = (String) parent.getItemAtPosition(position);
            checkAnswer(selectedChoice, index, view);
        });
    }

    private void checkAnswer(String selectedChoice, int questionIndex, View view) {
        String correctAnswer = correctAnswersList[questionIndex];
        View selectedView = choicesListView.getChildAt(choicesListView.getPositionForView(view));

        if (selectedChoice.equals(correctAnswer)) {
            selectedView.setBackgroundColor(Color.GREEN);
            correctAnswers++;
        } else {
            selectedView.setBackgroundColor(Color.RED);
            incorrectAnswers++;
            for (int i = 0; i < choicesListView.getChildCount(); i++) {
                if (choicesListView.getItemAtPosition(i).equals(correctAnswer)) {
                    choicesListView.getChildAt(i).setBackgroundColor(Color.GREEN);
                }
            }
        }
        choicesListView.setEnabled(false);
    }
}
