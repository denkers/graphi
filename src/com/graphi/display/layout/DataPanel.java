//=========================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi
//=========================================

package com.graphi.display.layout;

import com.graphi.graph.Edge;
import com.graphi.graph.GraphData;
import com.graphi.graph.GraphDataManager;
import com.graphi.graph.Node;
import com.graphi.graph.TableModelBean;
import com.graphi.util.transformer.EdgeRowListTransformer;
import com.graphi.util.transformer.NodeRowListTransformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class DataPanel extends JPanel implements ActionListener
{
    protected JTable vertexTable, edgeTable, computeTable;
    protected DefaultTableModel vertexDataModel, edgeDataModel, computationModel;
    protected final JTabbedPane dataTabPane;
    protected final JScrollPane vertexScroller, edgeScroller, computeScroller;
    protected JLabel comModelContextLabel;
    protected JButton comContextBtn;
    private static DataPanel instance;
    private NodeRowListTransformer nodeRowListTransformer;
    private EdgeRowListTransformer edgeRowListTransformer;

    public DataPanel()
    {
        setLayout(new BorderLayout());
        dataTabPane         =   new JTabbedPane();
        
        initTables();
        initDefaultTableModels();

        vertexScroller      =   new JScrollPane(vertexTable);
        edgeScroller        =   new JScrollPane(edgeTable);
        computeScroller     =   new JScrollPane(computeTable);
        vertexTable.setPreferredScrollableViewportSize(new Dimension(630, 500));

        comModelContextLabel    =   new JLabel("None");
        comContextBtn           =   new JButton("Change");
        
        JPanel compTabWrapper       =   new JPanel(new BorderLayout());
        JPanel compContextWrapper   =   new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel compContextTitle     =   new JLabel("Context: ");
        
        compContextTitle.setFont(new Font("Arial", Font.BOLD, 12));
        compContextWrapper.add(comContextBtn);
        compContextWrapper.add(compContextTitle);
        compContextWrapper.add(comModelContextLabel);
        compTabWrapper.add(compContextWrapper, BorderLayout.NORTH);
        compTabWrapper.add(computeScroller, BorderLayout.CENTER);
        comContextBtn.addActionListener(this);

        dataTabPane.addTab("Vertex table", vertexScroller);
        dataTabPane.addTab("Edge table", edgeScroller);
        dataTabPane.addTab("Computation table", compTabWrapper);
        
        add(dataTabPane);
    }
    
    public Vector getComputeTableData()
    {
        return computationModel.getDataVector();
    }
    
    public Vector getComputeTableColumns()
    {
        int columnCount     =   computationModel.getColumnCount();
        Vector columns      =   new Vector(columnCount);
        
        for(int i = 0; i < columnCount; i++)
            columns.add(computationModel.getColumnName(i));
        
        return columns;
    }
    
    private void initTables()
    {
        vertexDataModel     =   new DefaultTableModel();
        vertexTable         =   new ImmutableTable(vertexDataModel);

        edgeDataModel       =   new DefaultTableModel();
        edgeTable           =   new ImmutableTable(edgeDataModel);
        
        computationModel    =   new DefaultTableModel();
        computeTable        =   new ImmutableTable(computationModel);
        
    }
    
    public void clearComputeTable()
    {
        computationModel    =   new DefaultTableModel();
        setComputationContext(null);
        computeTable.setModel(computationModel);
    }
    
    public String getComputationContext()
    {
        return comModelContextLabel.getText();
    }
    
    public TableModelBean getCompModelBean()
    {
        String context  =   getComputationContext();
        return new TableModelBean(computationModel, context);
    }
    
    public void loadCompModelBean(TableModelBean compModel)
    {
        setComputationModel(compModel.getModel());
        setComputationContext(compModel.getDescription());
    }
    
    public void reloadGraphObjects()
    {
        Graph<Node, Edge> graph     =   GraphDataManager.getGraphDataInstance().getGraph();
        loadNodes(graph);
        loadEdges(graph);
    }

    public void loadNodes(Graph graph)
    {
        ArrayList<Node> vertices   =   new ArrayList<>(graph.getVertices());
        Collections.sort(vertices, (Node n1, Node n2) -> Integer.compare(n1.getID(), n2.getID()));

        GraphData data  =   GraphDataManager.getGraphDataInstance();
        data.getNodeFactory().setLastID(0);
        data.getNodes().clear();
        
        vertexDataModel.setRowCount(0);
        for(Node vertex : vertices)
        {
            int vID         =   vertex.getID();
            data.getNodes().put(vID, vertex);

            vertexDataModel.addRow(nodeRowListTransformer.transform(vertex).toArray());

            if(vID > data.getNodeFactory().getLastID())
                data.getNodeFactory().setLastID(vID);

        }
    }

    public void loadEdges(Graph graph)
    {
        ArrayList<Edge> edges  =   new ArrayList<>(graph.getEdges());
        Collections.sort(edges, (Edge e1, Edge e2) -> Integer.compare(e1.getID(), e2.getID())); 

        GraphData data  =   GraphDataManager.getGraphDataInstance();
        data.getEdges().clear();

        edgeDataModel.setRowCount(0);
        for(Edge edge : edges)
        {
            int eID                     =   edge.getID();

            data.getEdges().put(eID, edge);
            edgeRowListTransformer.setGraph(graph);
            edgeDataModel.addRow(edgeRowListTransformer.transform(edge).toArray());

            if(eID > data.getEdgeFactory().getLastID())
                data.getEdgeFactory().setLastID(eID);
        }
    }

    public void addVertex()
    {
        VertexAddPanel addPanel     =   new VertexAddPanel();
        GraphData data              =   GraphDataManager.getGraphDataInstance();

        int option  =   JOptionPane.showConfirmDialog(null, addPanel, "Add vertex", JOptionPane.OK_CANCEL_OPTION);

        if(option == JOptionPane.OK_OPTION)
        {
            int id      =   (int) addPanel.idSpinner.getValue();
            if(data.getNodes().containsKey(id))
            {
                JOptionPane.showMessageDialog(null, "Vertex already exists");
                return;
            }

            String name =   addPanel.nameField.getText();
            Node node   =   new Node(id, name);
            data.getGraph().addVertex(node);
            GraphPanel.getInstance().getGraphViewer().repaint();
            loadNodes(data.getGraph());
            data.getNodes().put(id, node);
        }
    }

    public void editVertex()
    {
        Node editNode;
        GraphData data                  =   GraphDataManager.getGraphDataInstance();
        Set<Node> selectedVertices      =   GraphPanel.getInstance().getGraphViewer().getPickedVertexState().getPicked();

        if(selectedVertices.size() == 1)
            editNode = selectedVertices.iterator().next();
        else
        {
            int[] selectedRows  =   vertexTable.getSelectedRows();
            if(selectedRows.length == 1)
            {
                int id      =   (int) vertexDataModel.getValueAt(selectedRows[0], 0);
                editNode    =   data.getNodes().get(id);   
            }

            else
            {
                int id      =   getDialogID("Enter vertex ID to edit", data.getNodes());

                if(id != -1)
                    editNode    =   data.getNodes().get(id);
                else
                    return;
            }
        }

        VertexAddPanel editPanel    =   new VertexAddPanel();
        editPanel.idSpinner.setValue(editNode.getID());
        editPanel.nameField.setText(editNode.getName());
        editPanel.idSpinner.setEnabled(false);
        editPanel.autoCheck.setVisible(false);

        int option  =   JOptionPane.showConfirmDialog(null, editPanel, "Edit vertex", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION)
        {
            editNode.setID((int) editPanel.idSpinner.getValue());
            editNode.setName(editPanel.nameField.getText());
            loadNodes(data.getGraph());
        }
    }

    protected void removeVertices(Set<Node> vertices)
    {
        if(vertices.isEmpty()) return;

        GraphData data              =   GraphDataManager.getGraphDataInstance();
        VisualizationViewer viewer  =   GraphPanel.getInstance().getGraphViewer();
        
        for(Node node : vertices)
        {
            
            int id  =   node.getID();
            data.getNodes().remove(id);
            data.getGraph().removeVertex(node);
            viewer.repaint();
            loadNodes(data.getGraph());
        }
    }

    public void removeVertex()
    {
        VisualizationViewer viewer  =   GraphPanel.getInstance().getGraphViewer();
        Set<Node> pickedNodes       =   viewer.getPickedVertexState().getPicked();
        GraphData graphData         =   GraphDataManager.getGraphDataInstance();
        
        if(!pickedNodes.isEmpty())
            removeVertices(pickedNodes);

        else
        {
            int[] selectedRows  =   vertexTable.getSelectedRows();
            if(selectedRows.length > 0)
            {
                Set<Node> selectedNodes  =   new HashSet<>();
                for(int row : selectedRows)
                {
                    int id          =   (int) vertexDataModel.getValueAt(row, 0);
                    Node current    =   graphData.getNodes().get(id);

                    if(current != null)
                        selectedNodes.add(current);
                }

                removeVertices(selectedNodes);
            }

            else
            {
                int id  =   getDialogID("Enter vertex ID to remove", graphData.getNodes());
                if(id != -1)
                {
                    Node removedNode    =   graphData.getNodes().remove(id);
                    graphData.getGraph().removeVertex(removedNode);
                    loadNodes(graphData.getGraph());
                    viewer.repaint();
                }
            }
        }
    }

    public void addEdge()
    {
        EdgeAddPanel addPanel   =   new EdgeAddPanel();
        GraphData data          =   GraphDataManager.getGraphDataInstance();

        int option  =   JOptionPane.showConfirmDialog(null, addPanel, "Add edge", JOptionPane.OK_CANCEL_OPTION);

        if(option == JOptionPane.OK_OPTION)
        {
            int id  =   (int) addPanel.idSpinner.getValue();
            if(data.getEdges().containsKey(id))
            {
                JOptionPane.showMessageDialog(null, "Edge ID already exists");
                return;
            }

            int fromID          =   (int) addPanel.fromSpinner.getValue();
            int toID            =   (int) addPanel.toSpinner.getValue();
            double weight       =   (double) addPanel.weightSpinner.getValue();
            int eType           =   addPanel.edgeTypeBox.getSelectedIndex();
            EdgeType edgeType   =   (eType == 0)? EdgeType.UNDIRECTED : EdgeType.DIRECTED;

            if(data.getNodes().containsKey(fromID) && data.getNodes().containsKey(toID))
            {
                Edge edge       =   new Edge(id, weight);
                Node n1         =   data.getNodes().get(fromID);
                Node n2         =   data.getNodes().get(toID);

                data.getEdges().put(id, edge);
                data.getGraph().addEdge(edge, n1, n2, edgeType);
                loadEdges(data.getGraph());
                GraphPanel.getInstance().getGraphViewer().repaint();
            }

            else JOptionPane.showMessageDialog(null, "Vertex ID does not exist");
        }
    }

    public void editEdge()
    {
        Edge editEdge;
        Set<Edge> selectedEdges =   GraphPanel.getInstance().getGraphViewer().getPickedEdgeState().getPicked();
        GraphData data          =   GraphDataManager.getGraphDataInstance();
        
        if(selectedEdges.size() == 1)
            editEdge    =   selectedEdges.iterator().next();
        else
        {
            int[] selectedRows  =   edgeTable.getSelectedRows();
            if(selectedRows.length == 1)
            {
                int id      =   (int) edgeDataModel.getValueAt(selectedRows[0], 0);
                editEdge    =   data.getEdges().get(id);
            }

            else
            {
                int id  =   getDialogID("Enter edge ID to edit", data.getEdges());

                if(id != -1)
                    editEdge    =   data.getEdges().get(id);
                else
                    return;
            }
        }

        EdgeAddPanel editPanel  =   new EdgeAddPanel();
        editPanel.idSpinner.setValue(editEdge.getID());
        
        Graph<Node, Edge> graph =   data.getGraph();
        Node sourceNode         =   graph.getSource(editEdge);
        Node destNode           =   graph.getDest(editEdge);
        EdgeType eType          =   graph.getEdgeType(editEdge);
        
        editPanel.fromSpinner.setValue(sourceNode.getID());
        editPanel.toSpinner.setValue(destNode.getID());
        editPanel.weightSpinner.setValue(editEdge.getWeight());
        editPanel.edgeTypeBox.setSelectedIndex(eType == EdgeType.UNDIRECTED? 0 : 1);

        editPanel.idSpinner.setEnabled(false);
        editPanel.autoCheck.setVisible(false);

        int option  =   JOptionPane.showConfirmDialog(null, editPanel, "Edit edge", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION)
        {
            editEdge.setWeight((double) editPanel.weightSpinner.getValue());

            Node from   =   data.getNodes().get(Integer.parseInt(editPanel.fromSpinner.getValue().toString()));
            Node to     =   data.getNodes().get(Integer.parseInt(editPanel.toSpinner.getValue().toString()));

            graph.removeEdge(editEdge);
            graph.addEdge(editEdge, from, to, eType);

            loadEdges(graph);
            GraphPanel.getInstance().getGraphViewer().repaint();
        }
    }

    public void removeEdge()
    {
        Set<Edge> selectedEdges =   GraphPanel.getInstance().getGraphViewer().getPickedEdgeState().getPicked();
        GraphData data          =   GraphDataManager.getGraphDataInstance();
        
        if(selectedEdges.isEmpty())
        {
            int[] selectedRows  =   edgeTable.getSelectedRows();
            if(selectedRows.length > 0)
            {
                for(int row : selectedRows)
                {
                    int id          =   (int) edgeDataModel.getValueAt(row, 0);
                    Edge current    =   data.getEdges().remove(id);
                    data.getGraph().removeEdge(current);
                }
            }

            else
            {
                int id  =   getDialogID("Enter edge ID to remove", data.getEdges());

                if(id != -1)
                {
                    Edge removeEdge =   data.getEdges().remove(id);
                    data.getGraph().removeEdge(removeEdge);
                }

                else return;
            }
        }

        else
        {
            for(Edge edge : selectedEdges)
            {
                data.getEdges().remove(edge.getID());
                data.getGraph().removeEdge(edge);
            }
        }

        loadEdges(data.getGraph());
        GraphPanel.getInstance().getGraphViewer().repaint();
    }

    protected int getDialogID(String message, Map collection)
    {
        String idStr = JOptionPane.showInputDialog(message);
        int id = -1;
        while(idStr != null && id == -1)
        {
            try
            {
                id  =   Integer.parseInt(idStr);
                if(collection.containsKey(id))
                    break;
                else
                {
                    id      =   -1;
                    JOptionPane.showMessageDialog(null, "ID was not found");
                    idStr   =   JOptionPane.showInputDialog(message);
                }
            }

            catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null, "Invalid input found");
                idStr = JOptionPane.showInputDialog(message);
            }
        }

        return id;
    }
    
    public void resetDefaultTableModels()
    {
        setVertexDataModel(null);
        setEdgeDataModel(null);
        setComputationModel(null);
        initDefaultTableModels();
    }

    public void initDefaultTableModels()
    {
        vertexDataModel.addColumn("NodeID");
        vertexDataModel.addColumn("Name");

        edgeDataModel.addColumn("EdgeID");
        edgeDataModel.addColumn("FromVertex");
        edgeDataModel.addColumn("ToVertex");
        edgeDataModel.addColumn("Weight");
        edgeDataModel.addColumn("EdgeType");
        
        nodeRowListTransformer  =   new NodeRowListTransformer();
        edgeRowListTransformer  =   new EdgeRowListTransformer();
    }
    
    public JTable getComputeTable() 
    {
        return computeTable;
    }

    public void setComputeTable(JTable computeTable)
    {
        this.computeTable = computeTable;
    }

    public DefaultTableModel getComputationModel()
    {
        return computationModel;
    }
    
    public void setVertexDataModel(DefaultTableModel vertexDataModel)
    {
        if(vertexDataModel == null) vertexDataModel = new DefaultTableModel();
        
        this.vertexDataModel    =   vertexDataModel;
        vertexTable.setModel(vertexDataModel);
        vertexTable.tableChanged(new TableModelEvent(vertexDataModel));
    }
    
    public void setEdgeDataModel(DefaultTableModel edgeDataModel)
    {
        if(edgeDataModel == null) edgeDataModel = new DefaultTableModel();
        
        this.edgeDataModel  =   edgeDataModel;
        edgeTable.setModel(edgeDataModel);
        edgeTable.tableChanged(new TableModelEvent(edgeDataModel));
    }

    public void setComputationModel(DefaultTableModel computationModel) 
    {
        if(computationModel == null) computationModel = new DefaultTableModel();
        
        this.computationModel = computationModel;
        computeTable.setModel(computationModel);
        computeTable.tableChanged(new TableModelEvent(computationModel));
    }
    
    public void setComputationContext(String context)
    {
        if(context == null) context = "None";
        comModelContextLabel.setText(context);
    }
    
    public void changeComputationContext()
    {
        String context  =   JOptionPane.showInputDialog(null, "Enter the context of this table", "Table context", JOptionPane.OK_CANCEL_OPTION);
        setComputationContext(context);
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        Object src = e.getSource();
        
        if(src == comContextBtn)
            changeComputationContext();
    }
    
    
    
    private class ImmutableTable extends JTable
    {
        public ImmutableTable(DefaultTableModel model)
        {
            super(model);
        }
        
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
    }
    
     //--------------------------------------
    //  VERTEX ADD PANEL
    //--------------------------------------
            
    protected class VertexAddPanel extends JPanel implements ActionListener
    {
        protected JSpinner idSpinner;
        protected JCheckBox autoCheck;
        protected JTextField nameField;

        public VertexAddPanel()
        {
            setLayout(new MigLayout());
            idSpinner      =   new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));   
            autoCheck      =   new JCheckBox("Auto");
            nameField      =   new JTextField();

            autoCheck.setSelected(true);
            idSpinner.setValue(GraphDataManager.getGraphDataInstance().getNodeFactory().getLastID() + 1);
            idSpinner.setEnabled(false);

            add(new JLabel("ID "));
            add(idSpinner, "width :40:");
            add(autoCheck, "wrap");
            add(new JLabel("Name: "));
            add(nameField, "span 3, width :100:");
            autoCheck.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            Object src  =   e.getSource();
            if(src == autoCheck)
                idSpinner.setEnabled(!autoCheck.isSelected());
        }
    }

    //--------------------------------------
    //  EDGE ADD PANEL
    //--------------------------------------

    protected class EdgeAddPanel extends JPanel implements ActionListener
    {
        protected JSpinner idSpinner, fromSpinner, toSpinner, weightSpinner;
        protected JCheckBox autoCheck;
        protected JComboBox edgeTypeBox;

        public EdgeAddPanel()
        {
            setLayout(new MigLayout());
            idSpinner       =   new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
            fromSpinner     =   new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
            toSpinner       =   new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
            weightSpinner   =   new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 0.1));
            autoCheck       =   new JCheckBox("Auto");
            edgeTypeBox     =   new JComboBox();

            edgeTypeBox.addItem("Undirected");
            edgeTypeBox.addItem("Directed");

            idSpinner.setValue(GraphDataManager.getGraphDataInstance().getEdgeFactory().getLastID() + 1);
            idSpinner.setEnabled(false);
            autoCheck.setSelected(true);
            autoCheck.addActionListener(this);

            add(new JLabel("ID "));
            add(idSpinner, "width :40:");
            add(autoCheck, "wrap");
            add(new JLabel("From vertex ID"));
            add(fromSpinner, "span 2, width :40:, wrap");
            add(new JLabel("To vertex ID"));
            add(toSpinner, "span 2, width :40:, wrap");
            add(new JLabel("Weight"));
            add(weightSpinner, "span 2, width :70:, wrap");
            add(new JLabel("Type"));
            add(edgeTypeBox, "span 2");
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            Object src  =   e.getSource();
            if(src == autoCheck)
                idSpinner.setEnabled(!autoCheck.isSelected());
        }
    }
    
    public JTable getVertexTable() 
    {
        return vertexTable;
    }

    public JTable getEdgeTable() {
        return edgeTable;
    }

    public DefaultTableModel getVertexDataModel() 
    {
        return vertexDataModel;
    }

    public DefaultTableModel getEdgeDataModel() 
    {
        return edgeDataModel;
    }

    public JTabbedPane getDataTabPane() 
    {
        return dataTabPane;
    }

    public NodeRowListTransformer getNodeRowListTransformer()
    {
        return nodeRowListTransformer;
    }

    public void setNodeRowListTransformer(NodeRowListTransformer nodeRowListTransformer) 
    {
        this.nodeRowListTransformer = nodeRowListTransformer;
    }

    public EdgeRowListTransformer getEdgeRowListTransformer()
    {
        return edgeRowListTransformer;
    }

    public void setEdgeRowListTransformer(EdgeRowListTransformer edgeRowListTransformer)
    {
        this.edgeRowListTransformer = edgeRowListTransformer;
    }
    
    public static DataPanel getInstance()
    {
        if(instance == null) instance = new DataPanel();
        return instance;
    }
}