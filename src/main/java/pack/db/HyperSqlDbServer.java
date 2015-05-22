package pack.db;

import org.hsqldb.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

public class HyperSqlDbServer implements SmartLifecycle {
    private final Logger logger = LoggerFactory.getLogger(HyperSqlDbServer.class);
    private Server server;
    private boolean running = false;

    @Override
    public boolean isRunning() {
        if (server != null)
            server.checkRunning(running);
        return running;
    }

    @Override
    public void start() {
        if (server == null) {
            logger.info("Starting HSQL server...");
            server = new Server();
            try {
                server.putPropertiesFromFile("server.properties");
                server.setRestartOnShutdown(true);
                server.setPort(5431);
                server.setTls(true);
                server.start();
                server.getAddress();
                running = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            logger.info("Stopping HSQL server...");
            server.stop();
            running = false;
        }
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        stop();
        runnable.run();
    }
}