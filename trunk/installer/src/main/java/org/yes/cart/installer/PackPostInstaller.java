package org.yes.cart.installer;

import com.izforge.izpack.Pack;
import com.izforge.izpack.event.SimpleInstallerListener;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.util.AbstractUIProgressHandler;

/**
 * Base class for &lt;pack ...&gt; - bound post installation tasks.
 * Concrete implementation should be defined inside install.xml as &lt;listener&gt;.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">shyiko</a>
 */
public abstract class PackPostInstaller extends SimpleInstallerListener {

  private String packName;

  /**
   * @param packName name of the pack for which post installer will be executed
   */
  protected PackPostInstaller(String packName) {
    this.packName = packName;
  }

  @Override
  public void afterPack(Pack pack, Integer puckNumber, AbstractUIProgressHandler handler) throws Exception {
    if (packName.equals(pack.name)) {
      postInstall(getInstalldata());
    }
  }

  protected abstract void postInstall(AutomatedInstallData data) throws Exception;
}
