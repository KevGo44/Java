import sas.*;
import java.awt.Color;

public class Ufospiel
{
    private View fenster;
    Picture hintergrund;
    private Asteroid[] asteroiden;
    private Laser laser;
    private double destAst;
    private Ufo dasUfo;
    private Sound music;
    private char up;
    private char down;
    private char left;
    private char right;
    Text[] control;
    boolean steuerung;
    boolean abbruch;

    public Ufospiel()
    {
        fenster = new View(400,600);
        hintergrund = new Picture(0,0,"material/sterne.jpg");
        hintergrund.setHidden(true);
        asteroiden = new Asteroid[8];
        for (int i = 0; i < asteroiden.length; i++)
        {
            asteroiden[i] = new Asteroid();
            asteroiden[i].setHiddenTrue();
        }
        laser = new Laser(asteroiden);
        laser.setHiddenTrue();
        destAst = 0;
        dasUfo = new Ufo(asteroiden, laser);
        dasUfo.setHiddenTrue();
        steuerung = true;
        music = new Sound("material/turrican.wav");
        music.play();
        this.menu();
    }

    public static void main(String[] args)
    {
        new Ufospiel();
    }
    
    private void menu()
    {
        Picture loadingscreen;
        Text[] home;
        loadingscreen = new Picture(0,0,400,600,"material/videopaper.jpg");
        home = new Text[6];
        for (int i=0; i<home.length; i++)
        {
            home[i] = new Text(100,100+30*i,"",Color.WHITE);
        }
        home[0].setText("Start");
        home[1].setText("Einstellungen");
        home[2].setText("Ende");
        home[3].moveTo(100,25);
        home[4].moveTo(10,330);
        home[4].setText("Zur�ck");
        home[4].setHidden(true);
        home[5].setText("Programmed by Kevin K�ster");
        home[5].moveTo(10,567.5);
        control = new Text[9];
        for (int i=0; i<control.length; i++)
        {
            control[i] = new Text(10,0+30*i,"",Color.WHITE);
            control[i].setHidden(true);
        }
        control[0].setText("Hoch: \'w\'");
        up = 'w';
        down = 's';
        left = 'a';
        right = 'd';
        control[1].setText("Runter: \'s\'");
        control[2].setText("Links: \'a\'");
        control[3].setText("Rechts: \'d\'");
        control[4].setText("Schuss: \' \'");
        control[5].setText("10 zerst�rte Asteroiden = Speed +1");
        control[6].setText("Asteroiden: " + asteroiden.length);
        control[7].setText("Pause: \'q\'");
        control[8].setText("Steuerung �ndern");
        while (true)
        {
            home[3].setText("Zerst�rte Asteroiden: " + (int)destAst);
            if (home[0].mouseClicked())
            {
                destAst = 0;
                for (int i=0; i<home.length; i++)
                {
                    home[i].setHidden(true);
                }
                for (int i = 0; i < asteroiden.length; i++)
                {
                    asteroiden[i].setHiddenFalse();
                }
                loadingscreen.setHidden(true);
                hintergrund.setHidden(false);
                dasUfo.setHiddenFalse();
                this.fuehreAus();
                while (true)
                {
                    if (fenster.keyEnterPressed() || abbruch == true)
                    {
                        break;
                    }
                }
                for (int i=0; i<home.length; i++)
                {
                    home[i].setHidden(false);
                }
                home[4].setHidden(true);
                for (int i = 0; i < asteroiden.length; i++)
                {
                    asteroiden[i].reset();
                    asteroiden[i].setHiddenTrue();
                }
                loadingscreen.setHidden(false);
                hintergrund.setHidden(true);
                dasUfo.explosionHiddenTrue();
                dasUfo.reset();
                dasUfo.setHiddenTrue();
            }
            if (home[1].mouseClicked())
            {
                for (int i=0; i<home.length; i++)
                {
                    home[i].setHidden(true);
                } 
                home[4].setHidden(false);
                for (int i=0; i<control.length; i++)
                {
                    control[i].setHidden(false);
                }
                while (home[4].mouseClicked() == false)
                {
                    if (control[8].mouseClicked())
                        {
                        steuerung = !steuerung;
                        if (steuerung)
                        {
                            control[0].setText("Hoch: \'w\'");
                            control[1].setText("Runter: \'s\'");
                            control[2].setText("Links: \'a\'");
                            control[3].setText("Rechts: \'d\'");
                            up = 'w';
                            down = 's';
                            left = 'a';
                            right = 'd';
                        } else
                        {
                            control[0].setText("Hoch: \'ArrowUp\'");
                            control[1].setText("Runter: \'ArrowDown\'");
                            control[2].setText("Links: \'ArrowLeft'");
                            control[3].setText("Rechts: \'ArrowRight\'");
                            up = '8';
                            down = '5';
                            left = '4';
                            right = '6';
                        }
                    }
                }
                for (int i=0; i<home.length; i++)
                {
                    home[i].setHidden(false);
                }
                home[4].setHidden(true);
                for (int i=0; i<control.length; i++)
                {
                    control[i].setHidden(true);
                }
            }
            if (home[2].mouseClicked())
            {
                music.stop();
                break;
            }
        }
    }
    
    public void fuehreAus()
    {
        abbruch = false;
        Text[] pauseMenu = new Text[4];
        for (int i=0; i<pauseMenu.length; i++)
        {
            pauseMenu[i] = new Text(100,250+30*i,"",Color.WHITE);
            pauseMenu[i].setHidden(true);
        }
        pauseMenu[0].setText("Fortsetzen");
        pauseMenu[1].setText("Einstellungen");
        pauseMenu[2].setText("Men�");
        pauseMenu[3].moveTo(10,330);        
        pauseMenu[3].setText("Zur�ck");
        while(dasUfo.death() == false && abbruch == false)
        {
            if (fenster.keyPressed('q'))
            {
                for (int i=0; i<pauseMenu.length; i++)
                {
                    pauseMenu[i].setHidden(false);
                }
                pauseMenu[3].setHidden(true);
                while (true)
                {
                    if(pauseMenu[0].mouseClicked())
                    {
                        for (int i=0; i<pauseMenu.length; i++)
                        {
                            pauseMenu[i].setHidden(true);
                        }
                        break;
                    }
                    if(pauseMenu[1].mouseClicked())
                    {
                        for (int i=0; i<pauseMenu.length; i++)
                        {
                            pauseMenu[i].setHidden(true);
                        }
                        pauseMenu[3].setHidden(false);
                        for (int i=0; i<control.length; i++)
                        {
                            control[i].setHidden(false);
                        }
                        while (pauseMenu[3].mouseClicked() == false)
                        {
                            if (control[8].mouseClicked())
                            {
                                steuerung = !steuerung;
                                if (steuerung)
                                {
                                    control[0].setText("Hoch: \'w\'");
                                    control[1].setText("Runter: \'s\'");
                                    control[2].setText("Links: \'a\'");
                                    control[3].setText("Rechts: \'d\'");
                                    up = 'w';
                                    down = 's';
                                    left = 'a';
                                    right = 'd';
                                } else
                                {
                                    control[0].setText("Hoch: \'ArrowUp\'");
                                    control[1].setText("Runter: \'ArrowDown\'");
                                    control[2].setText("Links: \'ArrowLeft'");
                                    control[3].setText("Rechts: \'ArrowRight\'");
                                    up = '8';
                                    down = '5';
                                    left = '4';
                                    right = '6';
                                }
                            }
                        }
                        for (int i=0; i<control.length; i++)
                        {
                            control[i].setHidden(true);
                        }
                        for (int i=0; i<pauseMenu.length; i++)
                        {
                            pauseMenu[i].setHidden(false);
                        }
                        pauseMenu[3].setHidden(true);
                    }
                    if (pauseMenu[2].mouseClicked())
                    {
                        for (int i=0; i<pauseMenu.length; i++)
                        {
                            pauseMenu[i].setHidden(true);
                        }
                        abbruch = true;
                        break;
                    }
                }
            }
            music.play();
            if(left == '4')
            {
                if (fenster.keyLeftPressed())
                {
                    dasUfo.bewegeLinks(destAst/10+1);
                }
            } else
            {
                if (fenster.keyPressed(left))
                {
                    dasUfo.bewegeLinks(destAst/10+1);
                }
            }
            if(right == '6')
            {
                if (fenster.keyRightPressed())
                {
                    dasUfo.bewegeRechts(destAst/10+1);
                }
            } else
            {
                if (fenster.keyPressed(right))
                {
                    dasUfo.bewegeRechts(destAst/10+1);
                }
            }
            if(up == '8')
            {
                if (fenster.keyUpPressed())
                {
                    dasUfo.bewegeVorne(destAst/10+1);
                }
            } else
            {
                if (fenster.keyPressed(up))
                {
                    dasUfo.bewegeVorne(destAst/10+1);
                }
            }
            if(down == '5')
            {
                if (fenster.keyDownPressed())
                {
                    dasUfo.bewegeHinten(destAst/10+1);
                }
            } else
            {
                if (fenster.keyPressed(down))
                {
                    dasUfo.bewegeHinten(destAst/10+1);
                }
            }
            for (int i = 0; i < asteroiden.length;i++)
            {
                asteroiden[i].bewege(destAst/10+1);
            } 
            if (fenster.keyPressed(' '))
            {
                if (laser.freeToFire())
                {
                    laser.setHiddenFalse();
                    dasUfo.schuss();
                }
            }  
            if (dasUfo.shoot())
            {
                laser.move(destAst/10+2);
                if (laser.pDestAst())
                {
                    destAst++;
                    laser.pDestAstFalse();
                }
            }
            if (laser.freeToFire())
            {
                laser.setHiddenTrue();
                dasUfo.shootFalse();
            }
            fenster.wait(5);
            if (dasUfo.checkKill())
            {
                dasUfo.explodiere();
                laser.setHiddenTrue();
            }
        }
    }
}