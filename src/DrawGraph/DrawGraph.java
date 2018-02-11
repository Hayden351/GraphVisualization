package DrawGraph;

import Definition.FiniteAutomata;
import Definition.Transition;
import Definition.TransitionCondition;

import automatagenerator.AutomataGenerator;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.Random;
import java.io.StringReader;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import static processing.core.PApplet.atan2;
import static processing.core.PConstants.CENTER;

public class DrawGraph extends PApplet
{

    boolean ctrl_pressed = false;
    boolean s_pressed = false;
    int pictures = 0;

    enum CanvasMode
    {
        REGULAR,
        SCREEN_SHOT
    }
    public final static CanvasMode mode = CanvasMode.SCREEN_SHOT;
    //public final static CanvasMode mode = CanvasMode.REGULAR;

    public static final float vertexRadius = (mode == CanvasMode.REGULAR) ? 30 : 100;
    public static final float headingLen = (mode == CanvasMode.REGULAR) ? 10 : 20;

    // TODO: actually use this constant
    public static final int strokeWidth = (mode == CanvasMode.REGULAR) ? 1 : 5;
    static boolean presenting = true;

    // The graph that is drawn to the screen
    Graph G = new Graph();
    int time = millis();
    int pos;
    int fontSize = 13;

    ArrayList<ArrayList<Integer>> solutions = new ArrayList<>();

    public int altSign(int x)
    {
        return (x % 2 == 0) ? 1 : -1;
    }

    public int altMag(int x)
    {
        return (x % 2 == 0) ? 1 : 0;
    }

    public float magnitude(PVector v)
    {
        return (float) Math.pow(v.x * v.x + v.y * v.y, 0.5f);
    }

    // pythagoras theorem
    public float distance(Vertex a, Vertex b)
    {
        return distance(a.loc, b.loc);
    }

    public float distance(PVector a, PVector b)
    {
        return distance(a.x, a.y, b.x, b.y);
    }

    public float distance(float x1, float y1, float x2, float y2)
    {
        return (float) Math.pow(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2), .5f);
    }

    // generates a random float between lBound and rBound
    public float randomInRange(float lBound, float rBound)
    {
        return (new Random().nextFloat() * (rBound - lBound) + lBound);
    }

    public void drawHeading(float x, float y, float theta, float magnitude)
    {
        // vector in theta of heading
        float deltaX = (float) (Math.cos(theta) * magnitude);
        float deltaY = (float) (Math.sin(theta) * magnitude);

        //line(x, y, x + (float) deltaX, y + (float) deltaY);
        // lines perpendicular to heading
        line(x, y, x + (float) deltaY, y - (float) deltaX);
        line(x, y, x - (float) deltaY, y + (float) deltaX);

        // lines from perpendicular lines to heading line
        line(x + (float) deltaY, y - (float) deltaX, x + (float) deltaX, y + (float) deltaY);
        line(x - (float) deltaY, y + (float) deltaX, x + (float) deltaX, y + (float) deltaY);
    }

    class Pair
    {

        int left;
        int right;

        Pair(int left, int right)
        {
            this.left = left;
            this.right = right;
        }

    }

    public void setup()
    {
        surface.setResizable(true);
        G = generateGraphFromMatrix(
                "0 1 1 1 0\n"
                + "0 0 0 1 1\n"
                + "0 0 1 1 0\n"
                + "1 0 1 0 1\n"
                + "0 1 0 1 0\n"
        );

        // generate state machine
        //G = generateStateMachine("(PairReader)(1)(8)()(0~((~1 1~00~2 1~19~3 2~,,~4 3~09~3 3~,,~4 4~09~5 5~09~5 5~..~6 6~09~7 7~09~7 7~))~8 8~,,~0)");
        String matrix
                = "0 1 1 0 1 0 0 1 0 1 0 1 1\n"
                + "0 0 0 1 0 1 0 0 0 0 0 0 0\n"
                + "0 0 0 1 0 0 1 0 0 0 0 0 0\n"
                + "0 0 0 0 1 1 0 0 1 0 1 0 0\n"
                + "0 0 0 0 0 0 0 0 1 0 0 0 1\n"
                + "0 0 0 0 0 0 0 0 0 0 0 0 0\n"
                + "0 1 0 0 0 0 0 0 0 1 0 1 0\n"
                + "0 0 0 0 0 0 0 0 0 0 0 0 0\n"
                + "0 1 0 0 1 0 0 0 0 1 0 0 0\n"
                + "0 0 0 1 0 0 1 0 0 0 0 0 0\n"
                + "0 0 1 0 0 0 0 1 0 0 0 1 1\n"
                + "0 0 0 0 0 0 0 0 0 0 0 0 0\n"
                + "0 0 0 0 1 0 0 1 0 0 0 0 0\n";

        matrix
                = "0 1 1 0 0 0 0 0 0\n"
                + "0 0 0 1 0 0 0 0 0\n"
                + "0 0 0 1 0 0 1 0 0\n"
                + "0 0 0 0 1 1 0 0 0\n"
                + "0 0 0 0 0 0 0 0 0\n"
                + "0 0 0 0 0 0 1 0 0\n"
                + "0 0 0 0 0 0 0 1 0\n"
                + "0 0 0 0 0 0 0 0 1\n"
                + "0 0 0 0 0 0 0 0 0\n";
//        matrix = 
//                "0  1  1  1  1  1  0  0  1  0  1  0  1  0  0  1\n" +
//                "0  0  1  1  1  1  1  0  0  1  0  1  0  1  0  0\n" +
//                "0  0  0  1  0  1  1  1  1  0  1  0  0  0  1  0\n" +
//                "0  0  0  0  0  0  1  1  0  1  0  1  1  0  0  1\n" +
//                "0  0  0  0  0  1  1  1  1  1  0  0  1  0  1  0\n" +
//                "0  0  0  0  0  0  1  1  1  1  1  0  0  1  0  1\n" +
//                "0  0  0  0  0  0  0  1  0  1  1  1  1  0  1  0\n" +
//                "0  0  0  0  0  0  0  0  0  0  1  1  0  1  0  1\n" +
//                "0  0  0  0  0  0  0  0  0  1  1  1  1  1  0  0\n" +
//                "0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  0\n" +
//                "0  0  0  0  0  0  0  0  0  0  0  1  0  1  1  1\n" +
//                "0  0  0  0  0  0  0  0  0  0  0  0  0  0  1  1\n" +
//                "0  0  0  0  0  0  0  0  0  0  0  0  0  1  1  1\n" +
//                "0  0  0  0  0  0  0  0  0  0  0  0  0  0  1  1\n" +
//                "0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  1\n" +
//                "0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0\n" ;
//      
/*
        G = generateGraphFromMatrix(new LineAutomata().parseMatrix(matrix));
        //G = this.generate4QueenGraph();
        int i;
        boolean getAllSolutions = true;
        for (i = 0; i <= G.vertices.size(); i++)
        {
            solutions = CSCI4150.IndependentSet.run(matrix, i, getAllSolutions);
            if (solutions.isEmpty())
            {
                i--;
                break;
            }
        }
        
        solutions = CSCI4150.IndependentSet.run(matrix, i, getAllSolutions);
        
         */

        // problem 1 graph
        G = generateLabeledGraphFromMatrix(
                "* A B  C D E F\n"
                + "A * 5 10 * 1 *\n"
                + "B * *  2 * 9 4\n"
                + "C * *  * 5 * *\n"
                + "D * *  * * * 3\n"
                + "E * *  * * * 7\n"
                + "F * *  * * * *\n"
        );

        // problem 2c graph
//        G = generateGraphFromMatrix(
//            "0 1 1 1 1 0 0 0 0\n" +
//            "0 0 0 0 0 1 0 0 0\n" +
//            "0 0 0 0 0 0 1 0 0\n" +
//            "0 0 0 0 0 0 0 1 0\n" +
//            "0 0 0 0 0 0 0 0 1\n" +
//            "0 0 0 0 0 0 0 0 0\n" +
//            "0 0 0 0 0 0 0 0 0\n" +
//            "0 0 0 0 0 0 0 0 0\n" +
//            "0 0 0 0 0 0 0 0 0\n"
//        );
//        // problem 3d graph
//        G = generateGraphFromMatrix
//        (
//    "0 0 1 0 0 0 0 1 0 0 0 0\n" +
//    "0 0 1 0 0 0 0 1 0 0 0 0\n" +
//    "0 0 0 1 0 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 1 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 1 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 0 1 0 0 0 0 0\n" +
//    "0 0 0 0 0 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 0 0 0 1 0 0 0\n" +
//    "0 0 0 0 0 0 0 0 0 1 0 0\n" +
//    "0 0 0 0 0 0 0 0 0 0 1 0\n" +
//    "0 0 0 0 0 0 0 0 0 0 0 1\n" +
//    "0 0 0 0 0 0 0 0 0 0 0 0\n"
//        );
//        // problem 8 a matrix
//        G = generateGraphFromMatrix
//        (
//    "0 1 1 1 1 1 0 0\n" +
//    "0 0 0 0 0 0 1 0\n" +
//    "0 0 0 0 0 0 0 1\n" +
//    "0 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 0 0 0\n" 
//        );
//        
//        // problem 8 b matrix
//        G = generateGraphFromMatrix
//        (
//    "0 1 1 1 1 1 0 0\n" +
//    "0 0 0 0 0 0 1 0\n" +
//    "0 0 0 0 0 0 0 1\n" +
//    "0 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 1 0 0\n" +
//    "0 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 0 0 0\n" +
//    "0 0 0 0 0 0 0 0\n" 
//        );
        // a, b, c, d respectively
//        G = generatePruferGraph(0,0,0,1,5,4);
//        G = generatePruferGraph(4,0,0,3,4,4);
//        G = generatePruferGraph(0,1,2,3,4);
//        G = generatePruferGraph(2,2,2,2,2);
        // problem 3 graph
        //G = generateContainerGraph(asList(8,5,3),asList(8,0,0), asList(4,4,0));
        //G = generateContainerGraph(asList(5,6,7,5),asList(4,2,1,2), asList(1,6,1,1));
        //G = generateContainerGraph(asList(8,5,3),asList(7,8,8), asList(4,4,0));
        setupSaveWindow();

        // sets font 
        textFont(createFont("Monospaced.plain", fontSize));
    }

    private Graph generateContainerGraph(List<Integer> capacities, List<Integer> initial, List<Integer> desiredState)
    {
        for (int i = 0; i < initial.size(); i++)
        {
            // if a container conatins more than it can contain
            if (initial.get(i) > capacities.get(i))
            {
                initial.set(i, capacities.get(i));
            }
        }
        HashSet<CSCI4150.Data> stuff = CSCI4150.HW2Problem3.run(capacities, initial);
        Graph G = new Graph();
        G.directed = true;
        HashMap<ArrayList<Integer>, Vertex> map = new HashMap<>();
        TreeSet<ArrayList<Integer>> listVertices = new TreeSet<>(new CSCI4150.ListComparator());

        for (CSCI4150.Data thing : stuff)
        {
            listVertices.addAll(asList(thing.precedent, thing.consequent));
        }
        for (ArrayList<Integer> list : listVertices)
        {
            String data = "";
            for (Integer value : list)
            {
                data += String.format("%d\n", value);
            }
            map.put(list, G.addVertex(String.format("%s", data)));
        }
        for (CSCI4150.Data thing : stuff)
        {
            G.addEdge(map.get(thing.precedent), map.get(thing.consequent), String.format("P_%d,%d", thing.containerFrom, thing.containerTo));
        }
        //if (map.containsKey(new ArrayList(initial)))
        map.get(new ArrayList(initial)).startS = true;
        if (map.containsKey(desiredState))
        {
            map.get(desiredState).finalS = true;
        }
        return G;
    }

    public Graph generatePruferGraph(Integer... elements)
    {
        Graph G = new Graph(vertexRadius);
        for (int i = 0; i < elements.length + 2; i++)
        {
            G.addVertex(String.format("%d", i));
        }
        ArrayList<CSCI4150.Pair> edgeValues = CSCI4150.PruferGraph.generatePruferGraph(elements);

        for (CSCI4150.Pair pp : edgeValues)
        {
            G.addEdge(G.vertices.get(pp.left), G.vertices.get(pp.right));
        }

        return G;
    }

    public void draw()
    {
        background(255);
        if (millis() > time + 400)
        {
            if (pos + 1 < solutions.size())
            {
                pos++;
            }
            time = millis();
        }
        // TODO: replace this with a general more drawGraph(G);
        //       might also want to think of naming the class something
        //       different
        /*
        if (0 <= pos && pos < solutions.size())
        {
            ArrayList<Integer> list = solutions.get(pos);
            for (int i = 0 ;i < list.size(); i++)
            {
                for (Vertex vv : G.vertices)
                {
                    if (list.get(i).equals(Integer.parseInt(vv.message)))
                    {
                        textSize(30);
                        fill(0);
                        textAlign(CENTER);
                        stroke(255);
                        text(String.format("%d", i),vv.loc.x,vv.loc.y);
                        //ellipse(vv.loc.x,vv.loc.y,25,25);
                    }
                }
            }
        }
         */

        drawGraph();
        drawSaveWindow();

    }

    void drawGraph()
    {
        background(255);
        for (Vertex vv : G.vertices)
        {
            if (vv.held)
            {
                vv.loc.x = constrain(mouseX, vertexRadius + 1, width - vertexRadius - 1);
                vv.loc.y = constrain(mouseY, vertexRadius + 1, height - vertexRadius - 1);
            }
            vv.drawVertex();
        }
        for (Edge ee : G.edges)
        {
            ee.drawEdge();
        }
    }

    public void mousePressed()
    {
        for (Vertex vv : G.vertices)
        {
            if (distance(vv.loc, new PVector(mouseX, mouseY)) <= vertexRadius)
            {
                vv.held = true;
                break;
            }

        }
        selectSaveWindowElement();
    }

    public void mouseReleased()
    {
        for (Vertex vv : G.vertices)
        {
            if (vv.held)
            {
                vv.held = false;
                break;
            }
        }
        pressSaveWindowElements();
    }

    public void keyPressed()
    {
        /*
        if (keyCode == CONTROL)
        {
            ctrl_pressed = true;
        }

        if (key == 's')
        {
            s_pressed = true;
        }
         */

    }

    public void keyReleased()
    {
        /*
        if (keyCode == CONTROL)
        {
            ctrl_pressed = false;
        }
         */
        switch (key)
        {
            case 19:     // ctrl + s
                toggleWindow();
                /*
                save(String.format("C:\\Users\\Hayden\\OneDrive\\Processing\\Images\\Graph%d.png", pictures));
                System.out.printf("Graph Saved as Graph%d.png\n", pictures);
                pictures++;
                 */
                break;
            case 26:     // ctrl + z
                break;
        }
        sendToSaveWindowTextBox(keyCode, key);
    }

    public Graph generateProblem6aGraph()
    {
        Graph G = new Graph();

        for (int i = 0; i < 6; i++)
        {
            Vertex carbon = new Vertex(vertexRadius, (i + 2) * (width / 9) + vertexRadius + 1, height / 2, "C");

            G.vertices.add(carbon);

            if (i != 0)
            {
                G.addEdge(new Edge(G.vertices.get(i - 1), G.vertices.get(i)));
            }

        }
        Vertex firstCarbon = G.vertices.get(0);
        Vertex lastCarbon = G.vertices.get(5);
        Vertex firstHydrogen = new Vertex(vertexRadius, firstCarbon.loc.x - 5 * vertexRadius, firstCarbon.loc.y, "H");
        Vertex lastHydrogen = new Vertex(vertexRadius, lastCarbon.loc.x + 5 * vertexRadius, lastCarbon.loc.y, "H");
        G.vertices.add(firstHydrogen);
        G.vertices.add(lastHydrogen);
        G.addEdge(new Edge(firstCarbon, firstHydrogen));
        G.addEdge(new Edge(lastCarbon, lastHydrogen));

        for (int i = 0; i < 6; i++)
        {
            Vertex carbon = G.vertices.get(i);
            Vertex hydrogenUp = new Vertex(vertexRadius, carbon.loc.x, carbon.loc.y - 5 * vertexRadius, "H");
            Vertex hydrogenDown = new Vertex(vertexRadius, carbon.loc.x, carbon.loc.y + 5 * vertexRadius, "H");
            G.vertices.add(hydrogenUp);
            G.vertices.add(hydrogenDown);
            G.addEdge(new Edge(carbon, hydrogenUp));
            G.addEdge(new Edge(carbon, hydrogenDown));
        }
        System.out.printf("%d %d\n", G.vertices.size(), G.edges.size());
        return G;
    }

    public Graph generateBipartiteGraph(int leftNodes, int rightNodes)
    {

        return generateBipartiteGraph(leftNodes, rightNodes, new ArrayList<Pair>());
    }

    public Graph generateBipartiteGraph(int leftNodes, int rightNodes, ArrayList<Pair> r)
    {

        Graph G = new Graph();

        //Graph theory Homework 1 problem 2
        for (int i = 0; i < leftNodes; i++)
        {
            float xPos = randomInRange(width / 4 + vertexRadius, width / 2 - 4 * vertexRadius);
            float yPos = randomInRange(vertexRadius + 1, height - vertexRadius - 1);
            G.vertices.add(new Vertex(vertexRadius, xPos, yPos, String.format("E%d", i + 1)));
        }

        String[] temp =
        {
            "M", "D", "C"
        };
        for (int i = 0; i < rightNodes; i++)
        {
            float xPos = randomInRange(width / 2 + 4 * vertexRadius + 1, width - vertexRadius - 1);
            float yPos = randomInRange(vertexRadius + 1, height - vertexRadius - 1);
            G.vertices.add(new Vertex(vertexRadius, xPos, yPos, temp[i]));
        }

        for (Pair pp : r)
        {
            G.addEdge(new Edge(G.vertices.get(pp.left - 1), G.vertices.get(leftNodes + pp.right - 1)));
        }

        return G;
    }

    public static int factorial(int n)
    {
        int product = 1;
        for (int i = 2; i <= n; i++)
        {
            product *= i;
        }
        return product;
    }

    public static int permutation(int n, int k)
    {
        return factorial(n) / factorial(n - k);
    }

    public Graph generateDecisionTree(int depth)
    {
        Graph G = new Graph();
        Vertex root = new Vertex(vertexRadius, width / 2, vertexRadius + 1, "0");
        G.addVertex(root);
        generateDecisionTreeAux(G, 0, depth, root, 0);
        return G;
    }
    // we have n nodes = permutation(depth, currentDepth)
    // number of nodes at depth currentDepth
    // 
    // given n does 
    // space between nodes is S
    // we have n + 1 occurances of S
    // (n+1)*S pixels + 2r(n) pixels = width pixels
    // S = (width - 2rn) / (n+1)

    public void generateDecisionTreeAux(Graph acc, int currentDepth, int finalDepth, Vertex prev, int value)
    {

        int nodesAtDepth = permutation(finalDepth, currentDepth);
        float space = (width - 2 * vertexRadius * nodesAtDepth)
                / (nodesAtDepth + 1);
        float x = (value + 1) * (space + 2 * vertexRadius);
        prev.loc.x = x;

        if (currentDepth == finalDepth)
        {
            return;
        } else
        {
            // i = 0..4
            // i = -2..4
            int range = finalDepth - currentDepth;

            for (int i = 0; i < range; i++)
            {

                Vertex child;

                //child = new Vertex(vertexRadius, new PVector(x, (height / (finalDepth)) * (currentDepth + 1)),String.format("%d",value*range  + i));
                child = new Vertex(vertexRadius, new PVector(x, (height / (finalDepth)) * (currentDepth + 1)));
                acc.addVertex(child);
                acc.addEdge(new Edge(prev, child));
                generateDecisionTreeAux(acc, currentDepth + 1, finalDepth, child, value * range + i);
            }
        }
        return;
    }

    public Graph generateCompleteGraph(int numberOfNodes)
    {

        String graphRepresentation = "";
        for (int i = 0; i < numberOfNodes; i++)
        {
            for (int j = 0; j < i + 1; j++)
            {
                graphRepresentation += "1 ";
            }
            for (int j = i + 1; j < numberOfNodes; j++)
            {
                if (j != numberOfNodes - 1)
                {
                    graphRepresentation += String.format("%d ", 1);
                } else
                {
                    graphRepresentation += String.format("%d\n", 1);
                }
            }
        }
        System.out.printf("%s", graphRepresentation);
        Graph G = new Graph();
        Random generate = new Random();
        for (int i = 0; i < numberOfNodes; i++)
        {
            float xPos = constrain(width * (float) generate.nextDouble(), vertexRadius + 1, width - vertexRadius - 1);
            float yPos = randomInRange(vertexRadius + 1, height - vertexRadius - 1);
            G.vertices.add(new Vertex(vertexRadius, xPos, yPos, new Integer(i).toString()));
        }
        for (int i = 0; i < G.vertices.size(); i++)
        {
            for (int j = 0; j < i; j++)
            {
                G.addEdge(new Edge(G.vertices.get(i), G.vertices.get(j)));
            }
        }
        return generateGraphFromMatrix(graphRepresentation);
    }

    public Graph generateLabeledGraphFromMatrix(String matrix)
    {
        BufferedReader read = new BufferedReader(new StringReader(matrix));
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        String line;
        try
        {
            while ((line = read.readLine()) != null)
            {
                result.add(new ArrayList<>());

                for (String token : line.split("\\s*"))
                {
//                    if (token.equals("*"))
//                    {
//                        result.get(result.size()-1).add("");
//                    }
//                    else
                    {
                        result.get(result.size() - 1).add(token);
                    }
                }
            }
        } catch (IOException ioe)
        {
            System.err.printf("%s", ioe);
            return null;
        }
        for (ArrayList<String> row : result)
        {
            for (String element : row)
            {
                System.out.printf(" %s", element);
            }
            System.out.println();
        }

        return generateLabeledGraphFromMatrix(result);
    }

    public Graph generateLabeledGraphFromMatrix(ArrayList<ArrayList<String>> matrix)
    {
        Graph G = new Graph();
        for (int i = 1; i < matrix.size(); i++)
        {
            System.out.printf("%1s", matrix.get(i).get(0));
        }

        for (int i = 1; i < matrix.size(); i++)
        {
            float xPos = randomInRange(vertexRadius + 1, width - vertexRadius - 1);
            float yPos = randomInRange(vertexRadius + 1, height - vertexRadius - 1);
            G.vertices.add(new Vertex(vertexRadius, xPos, yPos, matrix.get(i).get(0)));
        }

        for (int i = 0; i < G.vertices.size(); i++)
        {
            for (int j = 0; j < G.vertices.size(); j++)
            {
                if (!matrix.get(i + 1).get(j + 1).equals("*"))
                {
                    G.addEdge(new Edge(G.vertices.get(i), G.vertices.get(j), matrix.get(i + 1).get(j + 1)));
                }
            }
        }
        return G;
    }

    public ArrayList<ArrayList<Integer>> generateMatrixFromGraph(Graph G)
    {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < G.vertices.size(); i++)
        {
            result.add(new ArrayList<Integer>());
            for (int j = 0; j < G.vertices.size(); j++)
            {
                result.get(i).add(0);
            }
        }
        for (Edge ee : G.edges)
        {
            for (int i = 0; i < G.vertices.size(); i++)
            {
                for (int j = 0; j < G.vertices.size(); j++)
                {
                    if (ee.edgeStart == G.vertices.get(i) && ee.edgeEnd == G.vertices.get(j))
                    {
                        result.get(i).set(j, 1);
                    }
                }
            }
        }
        return result;
    }

    public Graph generateGraphFromMatrix(String input)
    {
        return generateGraphFromMatrix(new LineAutomata().parseMatrix(input));
    }

    public Graph generateGraphFromMatrix(ArrayList<ArrayList<Integer>> matrix)
    {
        int enforceSquare = matrix.size();
        for (int i = 0; i < matrix.size(); i++)
        {
            if (matrix.get(i).size() != enforceSquare)
            {
                System.err.print("Graph is not a square!\n");
                return new Graph();
            }
        }
        Graph G = new Graph();
        for (int i = 0; i < matrix.size(); i++)
        {
            float xPos = randomInRange(vertexRadius + 1, width - vertexRadius - 1);
            float yPos = randomInRange(vertexRadius + 1, height - vertexRadius - 1);
            G.vertices.add(new Vertex(vertexRadius, xPos, yPos, new Integer(i).toString()));
        }

        for (int i = 0; i < matrix.size(); i++)
        {
            for (int j = 0/*i + 1*/; j < matrix.get(i).size(); j++)
            {
                if (matrix.get(i).get(j) >= 1)
                {
                    G.addEdge(new Edge(G.vertices.get(i), G.vertices.get(j), ""/*matrix.get(i).get(j).toString()*/));
                }
            }
        }

        return G;
    }

    // private method for generate8QueenGraph method
    private void addAll(Vertex[][] grid, int i, int j, int x, int y)
    {
        int posX = i;
        int posY = j;
        while (0 <= posX && posX < grid.length && 0 <= posY && posY < grid.length)
        {
            if (!(posX == i && posY == j))
            {
                G.addEdge(new Edge(grid[i][j], grid[posX][posY]));
            }
            posX += x;
            posY += y;

        }
    }

    public Graph generate4QueenGraph()
    {
        G = new Graph();
        G.directed = false;
        Vertex[][] grid = new Vertex[4][4];
        int pos = 0;
        for (int i = 0; i < grid.length; i++)
        {
            for (int j = 0; j < grid.length; j++)
            {

                grid[i][j] = new Vertex(vertexRadius, i * vertexRadius * 4 + 500, j * vertexRadius * 4 + 125, String.format("%d", pos++));
                G.addVertex(grid[i][j]);
            }
        }
        for (int i = 0; i < grid.length; i++)
        {
            for (int j = 0; j < grid.length; j++)
            {
                for (int x = -1; x <= 1; x++)
                {
                    for (int y = -1; y <= 1; y++)
                    {
                        if (x == 0 && y == 0)
                        {
                            continue;
                        }
                        addAll(grid, i, j, x, y);
                    }
                }
            }
        }
        for (ArrayList<Integer> row : generateMatrixFromGraph(G))
        {
            for (Integer element : row)
            {
                System.out.printf("%d  ", element);
            }
            System.out.println();
        }

        return G;
    }

    public Graph generateStateMachine(String input)
    {
        Graph G = new Graph();
        G.directed = true;
        FiniteAutomata fa = null;
        try

        {
            fa = AutomataGenerator.generateAutomata(input);
        } catch (FileNotFoundException fnfe)
        {
            System.err.printf("%s, generateStateMachine failed\n", fnfe);
        }

        if (fa != null)
        {
            for (Integer ss : fa.states)
            {
                Vertex temp = new Vertex(vertexRadius, String.format("%d", ss));
                if (!fa.initialState.isEmpty())
                {
                    if (fa.initialState.iterator().next().equals(ss))
                    {
                        temp.startS = true;
                    }
                }
                if (fa.finalStates.contains(ss))
                {
                    temp.finalS = true;
                }

                G.vertices.add(temp);
            }
            for (Transition tt : fa.delta)
            {
                for (Vertex vv : G.vertices)
                {
                    if (vv.message.equals(tt.antecedent.iterator().next().toString())) // find the vertex that matches the antecedent of the transition
                    {
                        for (Vertex uu : G.vertices)
                        {
                            if (uu.message.equals(tt.consequent.iterator().next().toString())) // find vertex that matches the consequent of the transition
                            {
                                G.addEdge(new Edge(vv, uu, tt.incidence.iterator().next().toString()));
                            }
                        }
                    }
                }
            }
        }
        return G;
    }

    private static int opt = 0;

    enum Option
    {
        DIRECTED,
        SIMPLE,
        A;
        private int code;

        private Option()
        {
            this.code = 1 << opt++;
        }
    }

    //TODO: Put vertex and edge into new files by facotring out drawEdge, drawVertex
    class Graph
    {

        int options = 0;
        float vertexRad;
        float headingLength;
        boolean directed;
        boolean labled;
        ArrayList<Vertex> vertices;
        ArrayList<Edge> edges;

        public Graph()
        {
            this(vertexRadius, false, 0);
        }

        public Graph(int vertexSize, int edgeWeight)
        {
            this();
        }

        public Graph(float vertexRadius)
        {
            this(vertexRadius, false, 0);
        }

        public Graph(float vertexRadius, boolean directed, float headingLength)
        {
            vertices = new ArrayList<Vertex>();
            edges = new ArrayList<Edge>();
            this.vertexRad = vertexRadius;
            this.directed = directed;
            this.headingLength = headingLength;
        }

        public Vertex addVertex(String msg)
        {
            return addVertex(new Vertex(vertexRadius, msg));
        }

        public Vertex addVertex(Vertex vv)
        {
            vertices.add(vv);
            return vv;
        }

        public boolean addEdge(Vertex left, Vertex right)
        {
            return addEdge(new Edge(left, right, ""));
        }

        public boolean addEdge(Vertex left, Vertex right, String msg)
        {
            return addEdge(new Edge(left, right, msg));
        }

        public boolean addEdge(Edge newEdge)
        {
            // TODO: this method looks awful but it works
            boolean isNew = true;
            newEdge.directed = directed;
            for (Edge ee : edges)
            {
                // edges are the same do not add
                if (newEdge.edgeEnd == ee.edgeEnd && newEdge.edgeStart == ee.edgeStart)
                {
                    isNew = false;
                    break;
                } // bi directional edge modify edge but do not add
                else if (newEdge.edgeEnd == ee.edgeStart && newEdge.edgeStart == ee.edgeEnd)
                {
                    // edge is now bidirectional
                    ee.bidirectional = true;
                    ee.otherMessage = newEdge.message;
                    isNew = false;
                    break;
                } else // edge is new edge
                {

                }
            }

            if (isNew)
            {
                edges.add(newEdge);
                return true;
            }
            return false;
        }
    }

    class Vertex
    {

        float vertexRadius;
        boolean held = false;
        boolean finalS = false;
        boolean startS = false;
        boolean labeled = true;
        PVector loc;
        String message;

        Vertex(float radius)
        {
            this(radius, new PVector(randomInRange(radius + 1, width - radius - 1),
                    randomInRange(radius + 1, height - radius - 1)), "");
        }

        Vertex(float radius, PVector coordinates)
        {
            this(radius, coordinates.x, coordinates.y);
        }

        Vertex(float radius, float x, float y)
        {
            this(radius, x, y, "");
        }

        Vertex(float radius, String msg)
        {
            this(radius, new PVector(randomInRange(radius + 1, width - radius - 1),
                    randomInRange(radius + 1, height - radius - 1)), msg);
        }

        Vertex(float radius, PVector coordinates, String msg)
        {
            this(radius, coordinates.x, coordinates.y, msg);
        }

        Vertex(float radius, float x, float y, String msg)
        {
            vertexRadius = radius;
            loc = new PVector(x, y);
            message = msg;
        }

        public void drawVertex()
        {
            // constrain vertex in the window
            loc.x = constrain(loc.x, vertexRadius + 1, width - vertexRadius - 1);
            loc.y = constrain(loc.y, vertexRadius + 1, height - vertexRadius - 1);

            // set pen
            stroke(0);
            strokeWeight(1);
            noFill();

            // draw vertex as a circle
            ellipse(loc.x, loc.y, vertexRadius * 2, vertexRadius * 2);

            // if state is a start state add a in arrow
            if (startS)
            {
                stroke(200);
                strokeWeight(3);
                line(loc.x - 2 * vertexRadius, loc.y, loc.x - vertexRadius - headingLen - 2, loc.y);
                stroke(0);
                strokeWeight(1);
                noFill();
                drawHeading(loc.x - vertexRadius - headingLen, loc.y, 0, headingLen);
            }

            // if state is a final state
            if (finalS)
            {
                stroke(0);
                strokeWeight(1);
                noFill();
                ellipse(loc.x, loc.y, vertexRadius * 2 - 10, vertexRadius * 2 - 10);
            }

            if (labeled)
            {
                fill(0);
                if (mode == CanvasMode.SCREEN_SHOT)
                {
                    textSize(30);
                } else
                {
                    textSize(12);
                }
                textAlign(CENTER);
                text(message, loc.x, loc.y);
            }

        }
    }

    class Edge
    {

        boolean bidirectional = false;
        boolean directed = false;
        boolean labeled = true;
        public Vertex edgeStart;
        public Vertex edgeEnd;
        String message;
        String otherMessage;

        public Edge(Vertex ss, Vertex ee)
        {
            this(ss, ee, "");
        }

        public Edge(Vertex ss, Vertex ee, String msg)
        {
            this(ss, ee, msg, false);
        }

        public Edge(Vertex ss, Vertex ee, String msg, boolean directed)
        {
            edgeStart = ss;
            edgeEnd = ee;
            this.message = msg;
            //this.directed = directed;
        }

        public void drawEdge()
        {
            if (edgeStart == edgeEnd)
            {

                double theta = Math.PI * 3 / 8;
                if (mode == CanvasMode.REGULAR)
                {
                    strokeWeight(1);
                } else
                {
                    strokeWeight(3);
                }
                stroke(200);
                // draw line up from edge of vertex on left side
                line(edgeStart.loc.x - vertexRadius * (float) Math.cos(theta),
                        edgeStart.loc.y - vertexRadius * (float) Math.sin(theta),
                        edgeStart.loc.x - vertexRadius * (float) Math.cos(theta),
                        edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius);

                // draw line up from edge of vertex on right sight
                line(edgeStart.loc.x + vertexRadius * (float) Math.cos(theta),
                        edgeStart.loc.y - (vertexRadius + (headingLen + 3) * (directed ? 1 : 0)) * (float) Math.sin(theta),
                        edgeStart.loc.x + vertexRadius * (float) Math.cos(theta),
                        edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius);

                // draw line between them
                line(edgeStart.loc.x - vertexRadius * (float) Math.cos(theta), edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius, edgeStart.loc.x + vertexRadius * (float) Math.cos(theta), edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius);

                // add arrow to tip of edge
                if (directed)
                {
                    strokeWeight(1);
                    stroke(0);
                    drawHeading(edgeStart.loc.x + vertexRadius * (float) Math.cos(theta),
                            edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - headingLen,
                            (float) (Math.PI / 2),
                            headingLen);
                }

            } else
            {
                // draw edge
                if (directed)
                {
                    if (bidirectional)
                    {
                        double theta = atan2(edgeStart.loc.y - edgeEnd.loc.y, edgeStart.loc.x - edgeEnd.loc.x);
                        double thetaOffset = Math.PI / 16;
                        double xStart;
                        double yStart;
                        double xEnd;
                        double yEnd;
                        //line((float)xStart,(float)yStart,(float)xEnd,(float)yEnd);
                        for (int i = 0; i < 2; i++)
                        {
                            // set color
                            stroke(200);
                            // set line width
                            if (mode == CanvasMode.REGULAR)
                            {
                                strokeWeight(1);
                            } else
                            {
                                strokeWeight(3);
                            }
                            xStart = edgeStart.loc.x + (vertexRadius + altMag(i + 1) * headingLen) * Math.cos(theta + Math.PI + altSign(i) * (thetaOffset));
                            yStart = edgeStart.loc.y + (vertexRadius + altMag(i + 1) * headingLen) * Math.sin(theta + Math.PI + altSign(i) * (thetaOffset));
                            xEnd = edgeEnd.loc.x + (vertexRadius + altMag(i) * headingLen) * Math.cos(theta + altSign(i + 1) * (thetaOffset));
                            yEnd = edgeEnd.loc.y + (vertexRadius + altMag(i) * headingLen) * Math.sin(theta + altSign(i + 1) * (thetaOffset));
                            line((float) xStart, (float) yStart, (float) xEnd, (float) yEnd);
                            stroke(0);
                            strokeWeight(1);
                            drawHeading((float) ((i % 2 == 1) ? xStart : xEnd), (float) ((i % 2 == 1) ? yStart : yEnd), (float) (theta + altMag(i) * Math.PI), headingLen);
                        }
                    } else
                    {
                        // set color
                        stroke(200);
                        // set line width
                        if (mode == CanvasMode.REGULAR)
                        {
                            strokeWeight(1);
                        } else
                        {
                            strokeWeight(3);
                        }
                        double theta = atan2(edgeStart.loc.y - edgeEnd.loc.y, edgeStart.loc.x - edgeEnd.loc.x);
                        double xStart = edgeStart.loc.x + vertexRadius * Math.cos(theta + Math.PI);
                        double yStart = edgeStart.loc.y + vertexRadius * Math.sin(theta + Math.PI);
                        double xEnd = edgeEnd.loc.x + (vertexRadius + headingLen) * Math.cos(theta);
                        double yEnd = edgeEnd.loc.y + (vertexRadius + headingLen) * Math.sin(theta);
                        line((float) xStart, (float) yStart, (float) xEnd, (float) yEnd);
                        stroke(0);
                        strokeWeight(1);
                        drawHeading((float) xEnd, (float) yEnd, (float) (theta + Math.PI), headingLen);
                    }

                } else // not directed
                {
                    // set color
                    stroke(200);
                    // set line width
                    if (mode == CanvasMode.REGULAR)
                    {
                        strokeWeight(1);
                    } else
                    {
                        strokeWeight(3);
                    }
                    double theta = atan2(edgeStart.loc.y - edgeEnd.loc.y, edgeStart.loc.x - edgeEnd.loc.x);
                    double xStart = edgeStart.loc.x + vertexRadius * Math.cos(theta + Math.PI);
                    double yStart = edgeStart.loc.y + vertexRadius * Math.sin(theta + Math.PI);
                    double xEnd = edgeEnd.loc.x + vertexRadius * Math.cos(theta);
                    double yEnd = edgeEnd.loc.y + vertexRadius * Math.sin(theta);
                    line((float) xStart, (float) yStart, (float) xEnd, (float) yEnd);
                }
            }

            // print text along the edge
            fill(0);
            textAlign(CENTER);
            stroke(255);
            if (edgeStart == edgeEnd)
            {
                // TODO: figure out what on earth theta is for
                double theta = Math.PI * 3 / 8;
                text(message,
                        (edgeStart.loc.x - vertexRadius * (float) Math.cos(theta) + (edgeStart.loc.x + vertexRadius * (float) Math.cos(theta))) / 2,
                        edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius);
            } else
            {
                if (bidirectional)
                {

                    // TODO: refactor this since this theta appears in multiple conditions
                    double theta = atan2(edgeStart.loc.y - edgeEnd.loc.y, edgeStart.loc.x - edgeEnd.loc.x) + (Math.PI / 2);
                    fill(0);
                    textAlign(CENTER);
                    stroke(255);
                    text(message, (edgeStart.loc.x + edgeEnd.loc.x) / 2 - (float) (vertexRadius / 2 * Math.cos(theta)), (edgeStart.loc.y + edgeEnd.loc.y) / 2 - (float) (vertexRadius / 2 * Math.sin(theta)));
                    text(otherMessage, (edgeStart.loc.x + edgeEnd.loc.x) / 2 + (float) (vertexRadius / 2 * Math.cos(theta)), (edgeStart.loc.y + edgeEnd.loc.y) / 2 + (float) (vertexRadius / 2 * Math.sin(theta)));
                }

            }
        }
    }

    public static class LineAutomata
    {
        // An enumerated type for the states of a finite state automata

        private static enum State
        {
            // set of non final states
            START_STATE(Type.NON_FINAL),
            // set of final states
            ZERO(Type.FINAL),
            NON_ZERO(Type.FINAL),
            SPACE_PARSED(Type.FINAL),
            // set of error states
            UNDEFINED_TRANSITION(Type.ERROR);

            // indicates whether the state is a final, nonfinal or error state 
            private enum Type
            {
                FINAL,
                NON_FINAL,
                ERROR;
            }

            private final Type type;

            // Initialize state with its type
            private State(Type type)
            {
                this.type = type;
            }
        }
        static ArrayList<Integer> data;
        static Integer value;

        public ArrayList<ArrayList<Integer>> parseMatrix(String input)
        {
            ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
            BufferedReader read = new BufferedReader(new StringReader(input));
            String line;
            LineAutomata tt = new LineAutomata();
            try
            {
                while ((line = read.readLine()) != null)
                {

                    ArrayList<Integer> temp = tt.evaluate(line);

                    result.add(temp);

                }
            } catch (IOException ioe)
            {
                System.err.printf("%s", ioe);
                return null;
            }

            return result;
        }

        public static ArrayList<Integer> evaluate(String input)
        {
            return evaluate(input, 0);
        }

        public static ArrayList<Integer> evaluate(String input, int position)
        {
            data = new ArrayList<Integer>();
            value = 0;
            State currentState = State.START_STATE;
            for (; currentState.type != State.Type.ERROR && position < input.length(); position++)
            {

                char symbol = input.charAt(position);
                currentState = transition(currentState, symbol);
            }     // end for
            switch (currentState)
            {
                case ZERO:
                case NON_ZERO:
                    data.add(value);
            }

            return (currentState.type == State.Type.FINAL) ? data : null;
        }

        public static State transition(State state, char symbol)
        {
            switch (state)
            {
                case START_STATE:
                    if (' ' == symbol || '\n' == symbol)
                    {
                        state = State.START_STATE;
                    } else if ('1' <= symbol && symbol <= '9')
                    {
                        state = State.NON_ZERO;
                        value *= 10;
                        value += symbol - '0';
                    } else if ('0' == symbol)
                    {
                        state = State.ZERO;
                        value *= 10;
                        value += symbol - '0';
                    } else
                    {
                        state = State.UNDEFINED_TRANSITION;
                    }
                    break;
                case ZERO:
                    if (' ' == symbol)
                    {
                        state = State.SPACE_PARSED;
                        data.add(value);
                        value = 0;
                    } else
                    {
                        state = State.UNDEFINED_TRANSITION;
                    }
                    break;
                case NON_ZERO:
                    if (' ' == symbol || '\n' == symbol)
                    {
                        state = State.SPACE_PARSED;
                        data.add(value);
                        value = 0;
                    } else if ('0' <= symbol && symbol <= '9')
                    {
                        state = State.NON_ZERO;
                        value *= 10;
                        value += symbol - '0';
                    } else
                    {
                        state = State.UNDEFINED_TRANSITION;
                    }
                    break;
                case SPACE_PARSED:
                    if ('0' == symbol)
                    {
                        state = State.ZERO;
                        value *= 10;
                        value += symbol - '0';
                    } else if (' ' == symbol || '\n' == symbol)
                    {
                        state = State.SPACE_PARSED;
                    } else if ('1' <= symbol && symbol <= '9')
                    {
                        state = State.NON_ZERO;
                        value *= 10;
                        value += symbol - '0';
                    } else
                    {
                        state = State.UNDEFINED_TRANSITION;
                    }
                    break;
                default:
                    state = State.UNDEFINED_TRANSITION;
            }     // end switch
            return state;
        }     // end transition()
    }     // end LineAutomata

// save window elements
    String saveText; // currently unused
    MovableElement saveButton;

    String closeText; // currently unused
    MovableElement closeButton;

    String barText;
    MovableElement window;

// no text its a window
    MovableElement windowBar;

    StringBuilder textFieldText;
    MovableElement textField;

    String outputFieldText;
    MovableElement outputField;

    void setupSaveWindow()
    {
        // bar that when selected will move the entire window to the mouse
        windowBar = new MovableElement(width / 2, height / 2, new ArrayList<PVector>(asList(new PVector(-200, -50), new PVector(-200, 25), new PVector(200, 25), new PVector(200, -50))));
        barText = "Graph Save Menu";

        // rectangle that represents the window (not enforced)
        window = new MovableElement(0, 0, new ArrayList<PVector>(asList(new PVector(-200, -200), new PVector(-200, 200), new PVector(200, 200), new PVector(200, -200))));

        // button that when pressed will close the window
        saveButton = new MovableElement(0, 0, new ArrayList<PVector>(asList(new PVector(-fontSize, 20), new PVector(70, 20), new PVector(70, -30), new PVector(-fontSize, -30))));

        // button that when pressed will save the sketch into a folder
        closeButton = new MovableElement(0, 0, new ArrayList<PVector>(asList(new PVector(-fontSize, 20), new PVector(70, 20), new PVector(70, -30), new PVector(-fontSize, -30))));

        // text field where user can enter the name they want to save
        textField = new MovableElement(0, 0, new ArrayList<PVector>(asList(new PVector(-fontSize, 25), new PVector(150, 25), new PVector(150, -28), new PVector(-fontSize, -28))));
        textFieldText = new StringBuilder();

        // text field where output is given (sketch saved or error occured)
        outputField = new MovableElement(0, 0, new ArrayList<PVector>(asList(new PVector(-fontSize, 20), new PVector(400 - 3 * fontSize, 20), new PVector(400 - 3 * fontSize, -30), new PVector(-fontSize, -30))));
        outputFieldText = "";

        // everything is attached to the window bar since the window bar is the only movable element
        windowBar.relatedElements.addAll(asList(
                new RelativeMovableElement(windowBar, window, new PVector(0, 150)),
                new RelativeMovableElement(windowBar, saveButton, new PVector(-75, 250)),
                new RelativeMovableElement(windowBar, closeButton, new PVector(75, 250)),
                new RelativeMovableElement(windowBar, textField, new PVector(0, 100)),
                new RelativeMovableElement(windowBar, outputField, new PVector(-200 + 2 * fontSize, 175))
        ));
    }

    void drawSaveWindow()
    {

        // draw every element to the screen
        fill(255);
        stroke(255);
        if (windowBar.visible)
        {
            rectMode(CENTER);
            rect(windowBar.x, windowBar.y + 150, 400, 400);
        }
        stroke(0);
        strokeWeight(1);
        textAlign(LEFT);
        windowBar.drawShape();
        window.drawShape();
        saveButton.drawShape();
        closeButton.drawShape();
        textField.drawShape();
        outputField.drawShape();

        // if window is visible display text
        if (windowBar.visible)
        {
            textSize(fontSize);

            fill(0);

            text("Save as:", textField.x - 100, textField.y);
            text(textFieldText.toString(), textField.x, textField.y);
            text(outputFieldText, outputField.x, outputField.y);
            text(barText, windowBar.x - 175, windowBar.y);
            text("Close", closeButton.x, closeButton.y);
            text("Save", saveButton.x, saveButton.y);
        }
    }
// if toggles the visibility of the window and
// resets the window

    void toggleWindow()
    {
        windowBar.toPosition(width / 2, height / 4);
        windowBar.visible = !windowBar.visible;
        if (!windowBar.visible)
        {
            windowBar.selected = false;
        }
        for (RelativeMovableElement rme : windowBar.relatedElements)
        {
            rme.relativeElement.visible = !rme.relativeElement.visible;
        }
        textFieldText = new StringBuilder();
        outputFieldText = "";
    }

    void sendToSaveWindowTextBox(int code, Character regular)
    {
        // delete characters in the text box
        if (code == BACKSPACE)
        {
            if (1 <= textFieldText.length())
            {
                textFieldText.deleteCharAt(textFieldText.length() - 1);
            }
        } // if the user types a key append to the text box
        else if (Character.isLetterOrDigit(regular))
        {
            if (textFieldText.length() <= 15)
            {
                textFieldText.append(String.format("%c", key));
            }
        }
    }

    void selectSaveWindowElement()
    {
        if (windowBar.visible)
        {
            if (windowBar.mouseIn())
            {
                windowBar.selected = true;
            }
        }
    }

    void pressSaveWindowElements()
    {
        if (windowBar.visible)
        {
            // if released on save button save the sketch
            if (saveButton.mouseIn())
            {
                // only save if there is text in the text box
                if (textFieldText.length() >= 1)
                {
                    outputFieldText = String.format("Graph saved as %s.png\n", textFieldText);
                    drawGraph();
                    save(String.format("C:\\Users\\Hayden\\OneDrive\\Processing\\Images\\%s.png", textFieldText));

                } else
                {
                    outputFieldText = String.format("Error no text in field");
                }

            }

            // close the window if the close button is pressed
            if (closeButton.mouseIn())
            {
                toggleWindow();
            }
        } // end if (windowBar.visible)

        // everything is deselected
        windowBar.selected = false;
    }

    class MovableElement
    {

        float x;
        float y;
        ArrayList<PVector> relativePoints;
        boolean selected = false;
        boolean visible;
        ArrayList<RelativeMovableElement> relatedElements;

        MovableElement(float x, float y, ArrayList<PVector> rPVectors)
        {
            this.x = x;
            this.y = y;
            relativePoints = new ArrayList<PVector>(rPVectors);
            relatedElements = new ArrayList<RelativeMovableElement>();
            visible = false;
        }

        public void addMovableElement(RelativeMovableElement combo, PVector point)
        {
            for (RelativeMovableElement rme : combo.relativeElement.relatedElements)
            {
                relatedElements.add(rme);
            }
        }

        public void drawShape()
        {

            if (visible)
            {
                fill(255);
                if (selected)
                {
                    toPosition(mouseX, mouseY);
                }
                stroke(0);
                /*
      if (isIn(new PVector(mouseX - x,mouseY - y),relativePoints))
      {
        stroke(0,255,255);
        
      }
      else
      {
        stroke(0); 
      }
                 */

                for (int i = 1; i <= relativePoints.size(); i++)
                {
                    line(x + relativePoints.get(i - 1).x, y + relativePoints.get(i - 1).y,
                            x + relativePoints.get(i % relativePoints.size()).x, y + relativePoints.get(i % relativePoints.size()).y);
                }
            }
        }

        public void toPosition(float xPos, float yPos)
        {
            x = constrain(xPos, 0, width);
            y = constrain(yPos, 0, height);
            for (RelativeMovableElement rme : relatedElements)
            {
                rme.relativeElement.x = constrain(xPos + rme.relativePosition.x, 0, width);
                rme.relativeElement.y = constrain(yPos + rme.relativePosition.y, 0, height);
            }
        }
        // returns true if the mouse is inside this element and false otherwise

        public boolean mouseIn()
        {
            return isIn(new PVector(mouseX - x, mouseY - y), relativePoints);
        }
    }

    class RelativeMovableElement
    {

        MovableElement relativeElement;
        PVector relativePosition;

        public RelativeMovableElement(MovableElement re, PVector rp)
        {
            this(re.x, re.y, re, rp);
        }

        public RelativeMovableElement(MovableElement me, MovableElement re, PVector rp)
        {
            this(me.x, me.y, re, rp);
        }

        public RelativeMovableElement(float x, float y, MovableElement re, PVector rp)
        {
            if (!(Math.abs(re.x - x) < 0.01f))
            {
                if (!(Math.abs(re.y - y) < 0.01f))
                {
                    re.x = x;
                    re.y = y;
                    re.x += rp.x;
                    re.y += rp.y;
                }
            }
            relativeElement = re;
            relativePosition = rp;
        }
    }

// determine if points are ordered counter clock-wise
    public boolean counterClockwise(PVector A, PVector B, PVector C)
    {
        // slope of CA > slope of BA
        return (C.y - A.y) * (B.x - A.x) > (B.y - A.y) * (C.x - A.x);
    }
// determines if AB intersects CD

    public boolean doesIntersect(PVector A, PVector B, PVector C, PVector D)
    {
        // 0   2 3: A is counter clockwise ordered to C and D
        // 1   2 3: B is counter clockwise ordered to C and D
        //          but not both counter clockwise ordered
        //   and
        // 0 1   2: C is counter clockwise ordered to A and B
        // 0 1   3: D is counter clockwise ordered to A and B
        //          but not both counter clockwise ordered
        // is sufficient to determine if two lines intersect 
        // except when they are parallel (not relevent here)
        return counterClockwise(A, B, C) ^ counterClockwise(A, B, D)
                && counterClockwise(A, C, D) ^ counterClockwise(B, C, D);
    }
// determines if the given point is inside the given polygon

    public boolean isIn(PVector point, ArrayList<PVector> polygon)
    {
        // parallel point at the edge of the canvas
        PVector extreme = new PVector(width, point.y);

        int intersectCount = 0;
        // counts the number of times the line from the point to  the extreme point 
        // intersects one of the sides of the polygon
        for (int currentPoint = 0, nextPoint = 1;
                currentPoint < polygon.size();
                currentPoint++, nextPoint = (currentPoint + 1) % polygon.size())
        {
            if (doesIntersect(polygon.get(currentPoint),
                    polygon.get(nextPoint), point, extreme))
            {
                intersectCount++;
            }
        }
        // inside iff point intersects an odd number of times
        return intersectCount % 2 == 1;
    }

    public void settings()
    {
        size(displayWidth, displayHeight);
    }

    static public void main(String[] passedArgs)
    {
        String[] appletArgs;
        if (presenting)
        {
            appletArgs = new String[]
            {
                "--present", "--window-color=#666666", "--hide-stop", "DrawGraph.DrawGraph"
            };
        } else
        {
            appletArgs = new String[]
            {
                "DrawGraph.DrawGraph"
            };
        }
        if (passedArgs != null)
        {
            PApplet.main(concat(appletArgs, passedArgs));
        } else
        {
            PApplet.main(appletArgs);
        }
    }
}
