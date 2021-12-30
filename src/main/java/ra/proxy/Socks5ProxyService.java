package ra.proxy;

import ra.common.Config;
import ra.common.Envelope;
import ra.common.SystemSettings;
import ra.common.messaging.MessageProducer;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;
import socks.Socks5Proxy;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Socks5ProxyService extends BaseService {

    private static final Logger LOG = Logger.getLogger(Socks5ProxyService.class.getName());

    public static final String OPERATION_CREATE_PROXY = "CREATE_PROXY";
    public static final String OPERATION_START_PROXY = "START_PROXY";
    public static final String OPERATION_STOP_PROXY = "STOP_PROXY";
    public static final String OPERATION_REMOVE_PROXY = "REMOVE_PROXY";

    private final Map<String, RASocks5Proxy> proxies = new HashMap<>();

    public Socks5ProxyService() {}

    public Socks5ProxyService(MessageProducer producer, ServiceStatusObserver observer) {
        super(producer, observer);
    }

    @Override
    public void handleDocument(Envelope e) {
        Route route = e.getRoute();
        String operation = route.getOperation();
        switch(operation) {
            case OPERATION_CREATE_PROXY: {
                if(isNull(e.getValue("ra.proxy.name"))) {
                    e.addErrorMessage("ra.proxy.name value required.");
                    break;
                }
                if(isNull(e.getValue("ra.proxy.host"))) {
                    e.addErrorMessage("ra.proxy.host value required.");
                    break;
                }
                if(isNull(e.getValue("ra.proxy.port"))) {
                    e.addErrorMessage("ra.proxy.port value required.");
                    break;
                }
                if(isNull(e.getValue("ra.proxy.target"))) {
                    e.addErrorMessage("ra.proxy.target value required.");
                    break;
                }
                String name = (String)e.getValue("ra.proxy.name");
                String host = (String)e.getValue("ra.proxy.host");
                int port = (Integer)e.getValue("ra.proxy.port");
                String target = (String)e.getValue("ra.proxy.target");
                try {
                    RASocks5Proxy proxy = new RASocks5Proxy(host, port);
                    proxy.setTarget(target);
//                    proxy.setAuthenticationMethod()
                    proxies.put(name, proxy);
                } catch (UnknownHostException ex) {
                    e.addErrorMessage("Unknown host: "+host);
                    break;
                }
                break;
            }
            case OPERATION_START_PROXY: {
                if(isNull(e.getValue("ra.proxy.name"))) {
                    e.addErrorMessage("ra.proxy.name value required.");
                    break;
                }
                String name = (String)e.getValue("ra.proxy.name");
                RASocks5Proxy proxy = proxies.get(name);

                break;
            }
            case OPERATION_STOP_PROXY: {
                if(isNull(e.getValue("ra.proxy.name"))) {
                    e.addErrorMessage("ra.proxy.name value required.");
                    break;
                }
                String name = (String)e.getValue("ra.proxy.name");
                RASocks5Proxy proxy = proxies.get(name);

                break;
            }
            case OPERATION_REMOVE_PROXY: {
                if(isNull(e.getValue("ra.proxy.name"))) {
                    e.addErrorMessage("ra.proxy.name value required.");
                    break;
                }
                String name = (String)e.getValue("ra.proxy.name");
                proxies.remove(name);
                break;
            }
            default:
                deadLetter(e); // Operation not supported
        }
    }

    @Override
    public boolean start(Properties p) {
        LOG.info("Starting...");
        updateStatus(ServiceStatus.STARTING);
        if(!super.start(p))
            return false;
        LOG.info("Loading properties...");
        try {
            config = Config.loadAll(p, "ra-proxy.config");
            if(nonNull(config.getProperty("ra.proxy.setup"))) {
                // TODO: Default setup
            }
        } catch (Exception e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }

        updateStatus(ServiceStatus.RUNNING);
        LOG.info("Started.");
        return true;
    }

    @Override
    public boolean shutdown() {
        LOG.info("Shutting down...");
        updateStatus(ServiceStatus.SHUTTING_DOWN);


        updateStatus(ServiceStatus.SHUTDOWN);
        LOG.info("Shutdown.");
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        LOG.info("Gracefully shutting down...");
        updateStatus(ServiceStatus.GRACEFULLY_SHUTTING_DOWN);


        updateStatus(ServiceStatus.GRACEFULLY_SHUTDOWN);
        LOG.info("Gracefully shutdown.");
        return true;
    }
}
