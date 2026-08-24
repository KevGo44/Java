import sas.*;

public class Asteroid
{
    private Picture stein;
    
    public Asteroid()
    {
        stein = new Picture(0,0,40,40,"material/asteroid.png");
        this.setzeZurueck(-40-Math.random()*600);
    }

    public void bewege(double pGeschw)
    {
        stein.move(0,pGeschw);
        if (stein.getShapeY() > 650)
        {
            this.setzeZurueck(-40);
        }
    }

    public void setzeZurueck(double pY)
    {
        stein.moveTo(Math.random()*(400-40),pY);
        stein.turnTo(Math.random()*360);
        stein.scaleTo(30+Math.random()*20,30+Math.random()*20);
    }
    
    public Picture gibStein()
    {
        return stein;
    }
    
    public void setHiddenTrue()
    {
        stein.setHidden(true);
    }
    
    public void setHiddenFalse()
    {
        stein.setHidden(false);
    }
    
    public void reset()
    {
        stein.reset();
        this.setzeZurueck(-40-Math.random()*600);
    }
}