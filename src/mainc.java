import jym.tel.ManageFrame;
import jym.wit.AboutDialog;
import jym.wit.LookandfeelFactory;


/**
 * 本程序的代码写的稀烂, 看后小心吐血
 *                       -- CatfoOD
 */
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
