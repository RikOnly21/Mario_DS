
package model;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
//strategy design pattern
public class GameUI {
    private List<UIDesign> uiDesignList = new ArrayList<>();
    public GameUI() {
    }
    public void addUIDesign(List<? extends UIDesign> uiDesignList) {
        this.uiDesignList.addAll(uiDesignList);
    }
    public void addUIDesign(UIDesign uiDesign) {
        uiDesignList.add(uiDesign);
    }
    
    public void clear(){
        uiDesignList.clear();
    }
    
    public void render(Graphics g){
        try {
        if(uiDesignList != null && uiDesignList.size() > 0) {
            for(int i = 0; i < uiDesignList.size() ; i++) {
                UIDesign design = uiDesignList.get(i);
                if(design != null) {
                    design.draw(g);
                }
            }
        }
        } catch(Exception ex) {
            
        }
    }
}
