package DrawGraph;

import DrawGraph.DrawGraph.Edge;
import DrawGraph.DrawGraph.Graph;
import DrawGraph.DrawGraph.Vertex;
import static java.util.Arrays.asList;
import java.util.TreeSet;

/**
 *
 * @author Hayden
 */
public class GraphOperations 
{
    public static boolean isComplete(Graph G)
    {
        boolean result = true;
        if (G.directed)
        {
        }
        else
        {
            for (Vertex start : G.vertices)
            {
                for (Vertex  end : G.vertices)
                {
                    if (inGExistsEdge(G,start,end))
                    {
                    }
                }
            }
        }
         
        return result;
    }
    public static boolean isPath(Graph G, Vertex vv, Vertex uu)
    {
        return isPath(G,vv,uu,Integer.MAX_VALUE);
    }
    
    // determine if there is a path from node vv to node uu
    public static boolean isPath(Graph G, Vertex vv, Vertex uu, int length)
    {
        Vertex pos = vv;
        
        // initially we have visted the current node
        TreeSet<Vertex> visited = new TreeSet<Vertex>(asList(pos));
        
        if (G.directed)
        {
            
        }
        else
        {
            // check for adjacent vertexs
            for (Edge ee : G.edges)
            {
                // self loop
                if (ee.edgeStart == pos && ee.edgeEnd == pos)
                {
                }
                else if (ee.edgeStart == pos)
                {
                    if (ee.edgeEnd == uu)
                    {
                        return true;
                    }
                    else
                    {
                     
                    }
                }
                else if (ee.edgeEnd == pos)
                {
                }
            }
        }
        return false;
    }
    // dijkstras
    public static Vertex[] shortestPath(Graph G, Vertex find)
    {
        int[] cost = new int[G.vertices.size()];
        Vertex[] prev = new Vertex[G.vertices.size()];
        for (Vertex uu : G.vertices)
        {
            int value = Integer.parseInt(uu.message);
            cost[value] = Integer.MAX_VALUE;
            prev[value] = null;
        }
        cost[Integer.parseInt(find.message)] = 0;
        TreeSet<Vertex> V = new TreeSet<Vertex>(G.vertices);
        while (!V.isEmpty())
        {
            int vertexNumber = cost[findSmallest(cost)];
            Vertex uu = G.vertices.get(0);
            for (Vertex temp : G.vertices)
                if (Integer.parseInt(temp.message) == vertexNumber)
                    uu = temp;
            V.remove(uu);
            // find each neighbor
            for (Vertex vv : G.vertices)
            {
                if (isNeighbor(G,uu,vv))
                {
                    int alt = cost[Integer.parseInt(uu.message)] + Integer.parseInt(findEdge(G, uu,vv).message);
                    if (alt < cost[Integer.parseInt(vv.message)])
                    {
                        cost[Integer.parseInt(vv.message)] = alt;
                        prev[Integer.parseInt(vv.message)] = uu;
                    }
                }
            }
        }
        return prev;
    }
    
    private static boolean isNeighbor(Graph G, Vertex vv, Vertex uu)
    {
        for (Edge ee : G.edges)
        {
            if (vv == ee.edgeEnd && uu == ee.edgeStart
            ||  uu == ee.edgeEnd && vv == ee.edgeStart)
            {
             return true;   
            }
        }
        return false;
    }
    
    // given a list of integers will return the
    // index of the element with the smallest value
    private static int findSmallest(int[] values)
    {
        int pos = 0;
        for (int i = 1; i < values.length; i++)
            if (values[pos] < values[i])
                pos = i;
        return pos;
    }
    
    public static boolean isPathAux(Graph G, Vertex vv, Vertex uu, int length, TreeSet<Vertex> visted)
    {
        return true;
    }
    
    private static boolean inGExistsEdge(Graph G, Vertex start, Vertex end)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static Edge findEdge(Graph G, Vertex uu, Vertex vv)
    {
        for (Edge ee : G.edges)
        {
            if (vv == ee.edgeEnd && uu == ee.edgeStart
            ||  uu == ee.edgeEnd && vv == ee.edgeStart)
            {
             return ee;   
            }
        }
        return null;
    }
}