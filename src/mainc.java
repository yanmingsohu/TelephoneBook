import jym.tel.ManageFrame;
import jym.wit.AboutDialog;
import jym.wit.LookandfeelFactory;



public class mainc {

	public static void main(String[] args) {
		AboutDialog ad = new AboutDialog(null);

		try {
			LookandfeelFactory.loadConfig();
		} catch(Exception e) {
			LookandfeelFactory.setStanceFeel("MistSilver");
		}
		new ManageFrame();
		try {
			Thread.sleep(200);
		} catch(Exception e){}
		
		ad.setVisible(false);
	}
}
