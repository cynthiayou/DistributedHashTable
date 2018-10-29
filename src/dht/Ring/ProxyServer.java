package dht.Ring;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dht.server.Command;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ProxyServer extends PhysicalNode {
    public static int numOfReplicas = 3;

	public ProxyServer(){
		super();
	}

	public static void initializeRing(){
        try {
            String xmlPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "dht" + File.separator + "Ring" + File.separator + "config_ring.xml";
            System.out.println(xmlPath);
            File inputFile = new File(xmlPath);
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputFile);
            // Read from the configuration file "config_ring.xml"
            numOfReplicas = Integer.parseInt(document.getRootElement().element("replicationLevel").getStringValue());
            int hashRange = Integer.parseInt(document.getRootElement().element("hashRange").getStringValue());
            int virtualNodesMapping = Integer.parseInt(document.getRootElement().element("virtualNodesMapping").getStringValue());
            Element nodes = document.getRootElement().element("nodes");
            List<Element> listOfNodes = nodes.elements();
            int numOfNodes = listOfNodes.size();
            BinarySearchList table = new BinarySearchList();
            HashMap<String, PhysicalNode> physicalNodes = new HashMap<>();

            for (int i = 0; i < numOfNodes; i++){
                String ip = listOfNodes.get(i).element("ip").getStringValue();
                int port = Integer.parseInt(listOfNodes.get(i).element("port").getStringValue());
                String nodeID = ip + "-" + Integer.toString(port);
                PhysicalNode node = new PhysicalNode(nodeID, ip, port, "active");
                physicalNodes.put(nodeID, node);
            }
            // If hashRange is 10000 and there are 10 physical nodes in total, then stepSize is 1000
            // The first physical node will start from 0 and map to virtual nodes of hash 0, 1000, 2000,...,9000
            // The second physical node will start from 100 and map to virtual nodes of hash 100, 1100, 2100,...,9100
            // ...
            // The last physical node will start from 900 and map to virtual nodes of hash 900, 1900, 2900,...,9900
            int stepSize = hashRange / physicalNodes.size();
            // Define the start hash value for hash nodes
            int start = 0;
            for (String id : physicalNodes.keySet()){
                List<VirtualNode> virtualNodes = new ArrayList<>();
                // Each physical node maps to 10 virtual nodes during initialization
                for (int i = start; i < hashRange; i += stepSize){
                    VirtualNode vNode = new VirtualNode(i, id);
                    virtualNodes.add(vNode);
                    table.add(vNode);
                }
                physicalNodes.get(id).setVirtualNodes(virtualNodes);
                start += hashRange / (physicalNodes.size() * virtualNodesMapping);
            }
            // Create a lookupTable and set it to every physical node
            LookupTable t = new LookupTable();
            t.setTable(table);
            t.setPhysicalNodeMap(physicalNodes);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            t.setEpoch(timestamp.getTime());

            for (PhysicalNode node : physicalNodes.values()){
                node.setLookupTable(t);
            }
            // Print out all the virtual nodes defined during initialization
//            for(VirtualNode node : t.getRing()) {
//                System.out.print(node.getHash() + " ");
//            }
//            System.out.println();
//            for (HashMap.Entry<String, PhysicalNode> entry : t.getPhysicalNodeMap().entrySet()){
//                System.out.println(entry.getKey() + " "+ entry.getValue());
//            }

        }catch(DocumentException e) {
            e.printStackTrace();
        }
    }

	public static String getFindInfo(String input) {
		return input.toUpperCase();
	}
	
	public static String getLoadBalanceResult(String node1, String node2) {
		return "load balance success";
	}
	

	
	public String getResponse(String commandStr) {
		Command command = new Command(commandStr);
		if (command.getAction().equals("find")) {
			return getFindInfo(command.getInput());
		}
		else if (command.getAction().equals("loadbalance")) {
			return getLoadBalanceResult(command.node1, command.node2);
		}
		else if (command.getAction().equals("add")) {
			String ip = command.getCommandSeries().get(0);
			int port = Integer.valueOf(command.getCommandSeries().get(1));
			String result = super.addNode(ip, port);
			return result;
		}
		else if (command.getAction().equals("remove")) {
			int hash = Integer.valueOf(command.getCommandSeries().get(0));
			String result = super.deleteNode(hash);
			return result;
//			return "remove";
		}
		else if (command.getAction().equals("info")) {
			return super.listNodes();
		}
		else {
			return "";
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ProxyServer proxy = new ProxyServer();
		//Initialize the ring cluster
		initializeRing();
    	System.out.println("server running at 9091");
        ServerSocket listener = new ServerSocket(9091);;

        try {
            while (true) {
            	Socket socket = listener.accept();
            	System.out.println("Connection accepted" + " ---- " + new Date().toString());
                try {
                	BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    PrintWriter out =
                            new PrintWriter(socket.getOutputStream(), true);
                	String msg;
                	while(true) {
                		msg = in.readLine();
                    	if (msg != null) {
                        	System.out.println("request received: " + msg + " ---- " + new Date().toString());

                            String response = proxy.getResponse(msg);
                            out.println(response);
                            System.out.println("Response sent: " + response);
                    	}
                    	else {
                    		System.out.println("connection end " + " ---- " + new Date().toString());
                    		break;
                    	}
                	}

                } finally {
                    socket.close();
                }
            }
        }
        finally {
            listener.close();
        }
		
	}

}