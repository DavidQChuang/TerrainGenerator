package davidqchuang.TerrainGenerator.Visualization;

import javax.swing.*;

import davidqchuang.TerrainGenerator.Biomes;
import davidqchuang.TerrainGenerator.HeightmapGenerator;
import davidqchuang.TerrainGenerator.HeightmapNode;
import davidqchuang.TerrainGenerator.NoiseGenerators.PerlinNoise;
import davidqchuang.TerrainGenerator.Tools.MoreMath;
import davidqchuang.TerrainGenerator.Tools.float2;
import davidqchuang.TerrainGenerator.Tools.int2;
import davidqchuang.TerrainGenerator.Visualization.HeightmapMeshDisplays.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

class IntPointer {
	public int v;

	public IntPointer(int v) {
		this.v = v;
	}
}

// output to file for debugging massive maps (even 256x256 is 65536 lines at one line per tile.)
//		PrintStream out = null;
//		try {
//			out = new PrintStream(new FileOutputStream("output.txt"));
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}
//  	System.setOut(out);


public class DemoViewer {
	// constants
	public static IntPointer view = new IntPointer(0);
	public static final int mapSize = 256;

	public static void main(String[] args) {
		// -----------------------------------------------------------------------------
		// Map generation.

		HeightmapGenerator map = new HeightmapGenerator(mapSize, mapSize, 0);
		var nodes = map.generateBaseHeightmap(int2.zero);
		map.distributeMoisture(nodes);

		// -----------------------------------------------------------------------------
		// 3D meshes for rendering.

		List<List<Triangle>> meshes = new ArrayList<List<Triangle>>();

		meshes.add((new HeightHMD()).GenerateMesh(nodes));
		meshes.add((new MoistureHMD()).GenerateMesh(nodes));
		meshes.add((new BiomeHMD()).GenerateMesh(nodes));
//		meshes.add(circle);
//		meshes.add(tcircle);
//		meshes.add(flatmap);

		List<String> meshnames = new ArrayList<String>();

		meshnames.add("Height Map");
		meshnames.add("Moisture Map");
		meshnames.add("Biome Map");
//		meshnames.add("Globe");
//		meshnames.add("Tectonic Plates Globe");
//		meshnames.add("Flattened Globe");

		// -----------------------------------------------------------------------------
		// Rendering.

		JFrame frame = new JFrame();
		Container pane = frame.getContentPane();
		RenderPanel renderPanel = new RenderPanel();

		renderPanel.meshes = meshes;
		renderPanel.view = view;

		pane.setLayout(new BorderLayout());

		RenderSliders(pane, renderPanel);
		RenderMapView(meshnames, frame, renderPanel);

		pane.add(renderPanel, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	private static void RenderSliders(Container pane, RenderPanel renderPanel) {

		// slider to control horizontal rotation
		JSlider headingSlider = new JSlider(-360, 360, 140);
		pane.add(headingSlider, BorderLayout.SOUTH);

		// slider to control vertical rotation
		JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -180, 90, -45);
		pane.add(pitchSlider, BorderLayout.EAST);

		headingSlider.addChangeListener(e -> renderPanel.repaint());
		pitchSlider.addChangeListener(e -> renderPanel.repaint());

		renderPanel.headingSlider = headingSlider;
		renderPanel.pitchSlider = pitchSlider;
	}

	private static void RenderMapView(List<String> meshnames, JFrame frame, RenderPanel renderPanel) {

		// controls for map view.
		final TextField viewNum = new TextField();
		viewNum.setBounds(20, 0, 20, 20);
		viewNum.setText(Integer.toString(view.v));
		frame.add(viewNum);

		final TextField viewName = new TextField();
		viewName.setBounds(40, 0, 100, 20);
		viewName.setText(meshnames.get(view.v));
		frame.add(viewName);

		for (int color = 0; color < Biomes.Names.length; color++) {
			final TextField f1 = new TextField();
			f1.setBounds(20, color * 20 + 40, 30, 20);
			f1.setText(Biomes.ShortNames[color]);
			frame.add(f1);

			final TextField f2 = new TextField();
			f2.setBounds(50, color * 20 + 40, 130, 20);
			f2.setText(Biomes.Names[color]);
			frame.add(f2);
		}

		Button b = new Button("+");
		b.setBounds(0, 0, 20, 20);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.v = Math.max(0, Math.min(meshnames.size() - 1, view.v + 1));
				viewNum.setText(Integer.toString(view.v));
				viewName.setText(meshnames.get(view.v));
			}
		});

		Button b2 = new Button("-");
		b2.setBounds(0, 20, 20, 20);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.v = Math.max(0, Math.min(meshnames.size() - 1, view.v - 1));
				viewNum.setText(Integer.toString(view.v));
				viewName.setText(meshnames.get(view.v));
			}
		});
		frame.add(b);
		frame.add(b2);

		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderPanel.repaint();
			}
		});
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderPanel.repaint();
			}
		});
	}
}
