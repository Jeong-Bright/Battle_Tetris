package kr.ac.jbnu.se.tetris.dialogs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import kr.ac.jbnu.se.tetris.Select;
import kr.ac.jbnu.se.tetris.TetrisGameManager;

public class GameOverDialog extends JDialog {

    TetrisGameManager manager;
    
    JLabel gameOverText = new JLabel("게임 종료!");

    JButton retryBtn = new JButton("재시작");
    
    JButton homeBtn = new JButton("메인화면");

    public GameOverDialog(TetrisGameManager ownerFrame) {
        super(ownerFrame, true);
        manager = ownerFrame;
        setUndecorated(true);
        setSize(170, 135);
        setLocationRelativeTo(null);
        setLayout(null);

        setComponentBounds();
        addListener();
        addComponent();
    }

    private void setComponentBounds() {
        gameOverText.setBounds(60, 10, 150, 30);
        retryBtn.setBounds(10, 45, 150, 35);
        homeBtn.setBounds(10, 90, 150, 35);
    }

    private void addListener() {
        retryBtn.addActionListener(e->{
            boolean isComputer = manager.isComputer();
            
            setVisible(false);
            manager.dispose();

            TetrisGameManager game = new TetrisGameManager();
            game.start(isComputer);
        });
        homeBtn.addActionListener(e->{
            setVisible(false);
            manager.dispose();

            Select mainPage = new Select();
            mainPage.setVisible(true);
        });
    }

    private void addComponent() {
        add(gameOverText);
        add(retryBtn);
        add(homeBtn);
    }
}
