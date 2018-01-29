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

public class DrawGraph extends PApplet
{
    boolean ctrl_pressed = false;
    boolean s_pressed = false;

    enum CanvasMode
    {
        REGULAR,
        SCREEN_SHOT
    }
    public final static CanvasMode mode = CanvasMode.SCREEN_SHOT;
    //public final static CanvasMode mode = CanvasMode.REGULAR;
    public static final float vertexRadius = (mode == CanvasMode.REGULAR) ? 30 : 100;
    public static final float headingLen = (mode == CanvasMode.REGULAR) ? 10 : 20;
    
    static boolean presenting = true;
    
    // The graph that is drawn to the screen
    Graph G = new Graph();

    public int altSign(int x){return (x%2==0)?1:-1;}
    public int altMag(int x){return (x%2==0)?1:0;}
    
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
    //  Random generate = new Random();
    //  float value = (generate.nextFloat() * (rBound - lBound)) + lBound;
    //  return value;

        return (new Random().nextFloat() * (rBound - lBound) + lBound);
    }
    public void drawHeading (float x, float y, float direction, float magnitude)
    {
            // line in direction of heading
        float deltaX = (float) (Math.cos(direction) * magnitude);
        float deltaY = (float) (Math.sin(direction) * magnitude);

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
                          "0 1 1 0\n" +
                          "0 0 0 1\n" +
                          "0 0 0 0\n" +
                          "0 0 0 1\n"
        );
     
    }

    public void draw()
    {
        checkKeys();
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
    }

    public void checkKeys()
    {
        if (ctrl_pressed && s_pressed)
        {
            System.out.print("\nGraph Saved\n");
            save("C:\\Users\\Hayden\\OneDrive\\Processing\\Images\\Graph.png");
        }
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
                save("C:\\Users\\Hayden\\OneDrive\\Processing\\Images\\Graph.png");
                System.out.print("Graph Saved\n");
            case 26:     // ctrl + z

        }

    }

    public Graph generateProblem6aGraph()
    {
        Graph G = new Graph();

        for (int i = 0; i < 6; i++)
        {
            Vertex carbon = new Vertex((i + 2) * (width / 9) + vertexRadius + 1, height / 2, "C");

            G.vertices.add(carbon);

            if (i != 0)
            {
                G.edges.add(new Edge(G.vertices.get(i - 1), G.vertices.get(i)));
            }

        }
        Vertex firstCarbon = G.vertices.get(0);
        Vertex lastCarbon = G.vertices.get(5);
        Vertex firstHydrogen = new Vertex(firstCarbon.loc.x - 5 * vertexRadius, firstCarbon.loc.y, "H");
        Vertex lastHydrogen = new Vertex(lastCarbon.loc.x + 5 * vertexRadius, lastCarbon.loc.y, "H");
        G.vertices.add(firstHydrogen);
        G.vertices.add(lastHydrogen);
        G.edges.add(new Edge(firstCarbon, firstHydrogen));
        G.edges.add(new Edge(lastCarbon, lastHydrogen));

        for (int i = 0; i < 6; i++)
        {
            Vertex carbon = G.vertices.get(i);
            Vertex hydrogenUp = new Vertex(carbon.loc.x, carbon.loc.y - 5 * vertexRadius, "H");
            Vertex hydrogenDown = new Vertex(carbon.loc.x, carbon.loc.y + 5 * vertexRadius, "H");
            G.vertices.add(hydrogenUp);
            G.vertices.add(hydrogenDown);
            G.edges.add(new Edge(carbon, hydrogenUp));
            G.edges.add(new Edge(carbon, hydrogenDown));
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
            G.vertices.add(new Vertex(xPos, yPos, String.format("E%d", i + 1)));
        }

        String[] temp =
        {
            "M", "D", "C"
        };
        for (int i = 0; i < rightNodes; i++)
        {
            float xPos = randomInRange(width / 2 + 4 * vertexRadius + 1, width - vertexRadius - 1);
            float yPos = randomInRange(vertexRadius + 1, height - vertexRadius - 1);
            G.vertices.add(new Vertex(xPos, yPos, temp[i]));
        }

        for (Pair pp : r)
        {
            G.edges.add(new Edge(G.vertices.get(pp.left - 1), G.vertices.get(leftNodes + pp.right - 1)));
        }

        return G;
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
            G.vertices.add(new Vertex(xPos, yPos, new Integer(i).toString()));
        }
        for (int i = 0; i < G.vertices.size(); i++)
        {
            for (int j = 0; j < i; j++)
            {
                G.edges.add(new Edge(G.vertices.get(i), G.vertices.get(j)));
            }
        }
        return generateGraphFromMatrix(graphRepresentation);
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
            G.vertices.add(new Vertex(xPos, yPos, new Integer(i).toString()));
        }

        for (int i = 0; i < matrix.size(); i++)
        {
            for (int j = 0/*i + 1*/; j < matrix.get(i).size(); j++)
            {
                if (matrix.get(i).get(j) >= 1)
                {
                    G.edges.add(new Edge(G.vertices.get(i), G.vertices.get(j), ""/*matrix.get(i).get(j).toString()*/));
                }
            }
        }

        return G;
    }

    public Graph generate8QueenGraph()
    {
        Graph G = new Graph();

        String[][] values =
        {
            {
                "1", "1", "0", "1"
            },
            {
                "0", "1", "1", "1"
            },
            {
                "1", "1", "1", "0"
            },
            {
                "1", "0", "1", "1"
            }
        };

        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                G.vertices.add(new Vertex(i * 350 + vertexRadius * 6 - 100, j * 350 + vertexRadius * 2, values[j][i]));
            }
        }

        return G;
    }

    public Graph generateStateMachine(String input)
    {
        Graph G = new Graph();
        FiniteAutomata fa = null;
        try
        {
            fa = AutomataGenerator.generateAutomata(input);
        }
        catch(FileNotFoundException fnfe)
        {
            System.err.printf("%s, generateStateMachine failed\n", fnfe);
        }
        if (fa != null)
        {
            for (Integer ss : fa.states)
            {
                Vertex temp = new Vertex(String.format("%d", ss));
                if (fa.initialState.iterator().next().equals(ss))
                    temp.startS = true;
                if (fa.finalStates.contains(ss))
                    temp.finalS=true;
                        
                G.vertices.add(temp);
            }
            for (Transition tt : fa.delta)
            {
                for (Vertex vv : G.vertices)
                {
                    if (vv.msg.equals(tt.antecedent.iterator().next().toString()))
                    {
                        for (Vertex uu : G.vertices)
                        {
                            if (uu.msg.equals(tt.consequent.iterator().next().toString()))
                            {
                                TransitionCondition tc = tt.incidence.iterator().next();
                                G.edges.add(new Edge(vv,uu,
                                        (tc.lBound  == tc.rBound)?
                                                String.format("\'%c\'",tc.lBound):
                                                String.format("\'%c\'..\'%c\'",tc.lBound,tc.rBound)));
                            }
                        }
                    }
                }
            }
        }
                
        /*
        Vertex a;
        Vertex b;
        Vertex c;
        Vertex d;

        Graph G = new Graph();
        a = new Vertex("0");
        a.startS = true;
        G.vertices.add(a);
        G.edges.add(new Edge(a, a, "\' \'"));

        b = new Vertex("1");
        G.vertices.add(b);
        G.edges.add(new Edge(a, a, "\' \'"));

        G.edges.add(new Edge(a, b, "("));
        G.edges.add(new Edge(b, b, "\' \'"));

        a = new Vertex("2");
        G.edges.add(new Edge(b, a, "0"));
        G.vertices.add(a);

        c = new Vertex("3");
        G.edges.add(new Edge(b, c, "1..9"));
        G.vertices.add(c);
        G.edges.add(new Edge(c, c, "0..9"));

        d = new Vertex("4");
        G.vertices.add(d);
        G.edges.add(new Edge(d, d, "\' \'"));
        G.edges.add(new Edge(a, d, "\' \'"));
        G.edges.add(new Edge(c, d, "\' \'"));

        a = new Vertex("5");
        G.vertices.add(a);
        G.edges.add(new Edge(a, a, "\' \'"));
        G.edges.add(new Edge(d, a, ","));

        d = new Vertex("6");
        G.vertices.add(d);
        G.edges.add(new Edge(d, d, "0..9"));
        G.edges.add(new Edge(a, d, "0..9"));

        a = new Vertex("7");
        G.vertices.add(a);
        G.edges.add(new Edge(a, a, "0..9"));
        G.edges.add(new Edge(d, a, "."));

        d = new Vertex("8");
        d.finalS = true;
        G.vertices.add(d);
        G.edges.add(new Edge(d, d, "\' \'"));
        G.edges.add(new Edge(a, d, ")"));

        a = new Vertex("9");
        G.vertices.add(a);
        G.edges.add(new Edge(a, a, "\' \'"));
        G.edges.add(new Edge(d, a, ","));
        G.edges.add(new Edge(a, b, "("));
*/
        return G;
    }

    class Graph
    {
        boolean directed;
        ArrayList<Vertex> vertices;
        ArrayList<Edge> edges;
        

        Graph()
        {
            vertices = new ArrayList<Vertex>();
            edges = new ArrayList<Edge>();
        }

        Graph(int vertexSize, int edgeWeight)
        {
            vertices = new ArrayList<Vertex>();
            edges = new ArrayList<Edge>();
        }
        public boolean addVertex(Vertex vv)
        {
            return vertices.add(vv);
        }
        public boolean addEdge(Edge ee)
        {
            return edges.add(ee);
        }
    }

    class Vertex
    {
            // determines i

        boolean held = false;
        boolean finalS = false;
        boolean startS = false;
        PVector loc;
        String msg;

        Vertex()
        {
            loc = new PVector(randomInRange(vertexRadius + 1, width / 2),
                    randomInRange(vertexRadius + 1, height / 2));
            msg = "";
        }

        Vertex(String msg)
        {
            loc = new PVector(randomInRange(vertexRadius + 1, width / 2),
                    randomInRange(vertexRadius + 1, height / 2));
            this.msg = msg;
        }

        Vertex(float x, float y)
        {
            loc = new PVector(x, y);
            msg = "";
        }

        Vertex(float x, float y, String msg)
        {
            loc = new PVector(x, y);
            this.msg = msg;
        }

        public void drawVertex()
        {

            loc.x = constrain(loc.x, vertexRadius + 1, width - vertexRadius - 1);
            loc.y = constrain(loc.y, vertexRadius + 1, height - vertexRadius - 1);
            
            stroke(0);
            strokeWeight(1);
            noFill();
            //ellipse(loc.x,loc.y - vertexRadius, vertexRadius * 2, vertexRadius);
            // draw vertex as a circle
            ellipse(loc.x, loc.y, vertexRadius * 2, vertexRadius * 2);
            
            // if state is a start state add a in arrow
            if (startS)
            {
                line(loc.x - 2 * vertexRadius,loc.y,loc.x - vertexRadius,loc.y);
                drawHeading(loc.x - vertexRadius - headingLen,loc.y,0,headingLen);
            }
            
            
            // if state is a final state
            if (finalS)
            {
                ellipse(loc.x, loc.y, vertexRadius * 2 - 10, vertexRadius * 2 - 10);
            }

            fill(0);
            if (mode == CanvasMode.SCREEN_SHOT)
            {
                textSize(30);
            } else
            {
                textSize(12);
            }
            textAlign(CENTER);
            text(msg, loc.x, loc.y);

        }
    }
    
    class Edge
    {

        boolean bidirectional = false;
        boolean directed = true;
        public Vertex edgeStart;
        public Vertex edgeEnd;
        String msg;

        public Edge(Vertex ss, Vertex ee)
        {
            this(ss,ee,"");
        }

        public Edge(Vertex ss, Vertex ee, String msg)
        {
            this(ss,ee,msg,false);
        }
        public Edge(Vertex ss, Vertex ee, String msg,boolean directed)
        {
            edgeStart = ss;
            edgeEnd = ee;
            this.msg = msg;
            //this.directed = directed;
        }
        public void drawEdge()
        {

            if (edgeStart == edgeEnd)
            {
                stroke(200);
                double theta = Math.PI * 3 / 8;
                strokeWeight(3);

                // line over
                
                // draw line up from edge of vertex on left side
                line(edgeStart.loc.x - vertexRadius * (float) Math.cos(theta),
                        edgeStart.loc.y - vertexRadius * (float) Math.sin(theta),
                        edgeStart.loc.x - vertexRadius * (float) Math.cos(theta),
                        edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius);

                
                
                // draw line up from edge of vertex on right sight
                line(edgeStart.loc.x + vertexRadius * (float) Math.cos(theta), 
                     edgeStart.loc.y - (vertexRadius + headingLen + 3) * (float) Math.sin(theta), 
                     edgeStart.loc.x + vertexRadius * (float) Math.cos(theta),
                     edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius);
                
                // draw line between them
                line(edgeStart.loc.x - vertexRadius * (float) Math.cos(theta), edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius, edgeStart.loc.x + vertexRadius * (float) Math.cos(theta), edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius);
                
                if (directed)
                {
                    strokeWeight(1);
                    stroke(0);
                    drawHeading(edgeStart.loc.x + vertexRadius * (float) Math.cos(theta), edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - headingLen, (float) (Math.PI / 2), headingLen);
                }

            }
            else
            {
                // draw edge
                if (directed)
                {
                    if (bidirectional)
                    {
                        double theta = atan2(edgeStart.loc.y - edgeEnd.loc.y,edgeStart.loc.x - edgeEnd.loc.x);
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
                                strokeWeight(1);
                            else
                                strokeWeight(3);                            
                            xStart = edgeStart.loc.x + (vertexRadius + altMag(i+1)*headingLen) * Math.cos(theta + Math.PI + altSign(i  )*(thetaOffset));
                            yStart = edgeStart.loc.y + (vertexRadius + altMag(i+1)*headingLen) * Math.sin(theta + Math.PI + altSign(i  )*(thetaOffset));
                            xEnd   = edgeEnd.loc.x   + (vertexRadius + altMag(i  )*headingLen) * Math.cos(theta +           altSign(i+1)*(thetaOffset));
                            yEnd   = edgeEnd.loc.y   + (vertexRadius + altMag(i  )*headingLen) * Math.sin(theta +           altSign(i+1)*(thetaOffset));
                            line((float)xStart,(float)yStart,(float)xEnd,(float)yEnd);
                            stroke(0);
                            strokeWeight(1);
                            drawHeading((float)((i%2==1)?xStart:xEnd),(float)((i%2==1)?yStart:yEnd),(float)(theta + altMag(i)*Math.PI),headingLen);
                        }
    //                        xStart = edgeStart.loc.x + vertexRadius * Mah.cos(theta + Math.PI - (Math.PI / 4));
    //                        yStart = edgeStart.loc.y + vertexRadius * Math.sin(theta + Math.PI - (Math.PI / 4));
    //                        xEnd = edgeEnd.loc.x + vertexRadius * Math.cos(theta + (Math.PI / 4));
    //                        yEnd = edgeEnd.loc.y + vertexRadius * Math.sin(theta + (Math.PI / 4));
    //                        line((float)xStart,(float)yStart,(float)xEnd,(float)yEnd);
                    }
                    else
                    {
                        // set color
                        stroke(200);
                        // set line width
                        if (mode == CanvasMode.REGULAR)
                            strokeWeight(1);
                        else
                            strokeWeight(3);                            
                        double theta = atan2(edgeStart.loc.y - edgeEnd.loc.y,edgeStart.loc.x - edgeEnd.loc.x);
                        double xStart = edgeStart.loc.x + vertexRadius * Math.cos(theta + Math.PI);
                        double yStart = edgeStart.loc.y + vertexRadius * Math.sin(theta + Math.PI);
                        double xEnd = edgeEnd.loc.x + (vertexRadius + headingLen)  * Math.cos(theta);
                        double yEnd = edgeEnd.loc.y + (vertexRadius + headingLen)  * Math.sin(theta);
                        line((float)xStart,(float)yStart,(float)xEnd,(float)yEnd);
                        stroke(0);
                        strokeWeight(1);
                        drawHeading((float)xEnd,(float)yEnd,(float)(theta + Math.PI),headingLen);
                    }

                }
                else // not directed
                {
                    // set color
                    stroke(200);
                    // set line width
                    if (mode == CanvasMode.REGULAR)
                        strokeWeight(1);
                    else
                        strokeWeight(3);                            
                    double theta = atan2(edgeStart.loc.y - edgeEnd.loc.y,edgeStart.loc.x - edgeEnd.loc.x);
                    double xStart = edgeStart.loc.x + vertexRadius * Math.cos(theta + Math.PI);
                    double yStart = edgeStart.loc.y + vertexRadius * Math.sin(theta + Math.PI);
                    double xEnd = edgeEnd.loc.x + vertexRadius * Math.cos(theta);
                    double yEnd = edgeEnd.loc.y + vertexRadius * Math.sin(theta);
                    line((float)xStart,(float)yStart,(float)xEnd,(float)yEnd);
                }
            }

            // print text along the edge
            fill(0);
            textAlign(CENTER);
            stroke(255);
            if (edgeStart == edgeEnd)
            {
                {
                    //Vertex a = edgeStart;
                    double theta = Math.PI * 3 / 8;
                    text(msg, (edgeStart.loc.x - vertexRadius * (float) Math.cos(theta) + (edgeStart.loc.x + vertexRadius * (float) Math.cos(theta))) / 2, edgeStart.loc.y - vertexRadius * (float) Math.sin(theta) - vertexRadius);
                }
            } 
            else
            {
                if (bidirectional)
                {
                }
                text(msg, (edgeStart.loc.x + edgeEnd.loc.x) / 2, (edgeStart.loc.y + edgeEnd.loc.y) / 2);
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
        } 
        else
        {
            appletArgs = new String[]
            {
                "DrawGraph.DrawGraph"
            };
        }
        if (passedArgs != null)
        {
            PApplet.main(concat(appletArgs, passedArgs));
        } 
        else
        {
            PApplet.main(appletArgs);
        }
    }
}
