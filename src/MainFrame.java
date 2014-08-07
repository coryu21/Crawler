import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainFrame extends JFrame implements ActionListener {
	Container contentPane;
	JPanel dateJPanel;
	JPanel categoryJPanel;
	JPanel searchJPanel;
	JPanel showJPanel;
	JTextField before_tf;
	JTextField after_tf;
	JCheckBox category_box[];
	JTextField save_tf;
	JLabel count_lb;
	JButton set_btn;
	JButton search_btn;

	public MainFrame() {
		createInterface();
	}

	public void createInterface() {
		this.setTitle("���̹� ���� ũ�Ѹ�");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = this.getContentPane();
		contentPane.setLayout(new FlowLayout());

		dateJPanel = new JPanel();
		dateJPanel.add(new JLabel("�Ⱓ    "));
		before_tf = new JTextField(8);
		after_tf = new JTextField(8);
		dateJPanel.add(before_tf);
		dateJPanel.add(new JLabel(" ~ "));
		dateJPanel.add(after_tf);

		categoryJPanel = new JPanel();
		category_box = new JCheckBox[7];
		category_box[0] = new JCheckBox("��ġ");
		category_box[1] = new JCheckBox("����");
		category_box[2] = new JCheckBox("��ȸ");
		category_box[3] = new JCheckBox("��Ȱ_��ȭ");
		category_box[4] = new JCheckBox("����");
		category_box[5] = new JCheckBox("IT_����");
		category_box[6] = new JCheckBox("����");
		categoryJPanel.add(category_box[0]);
		categoryJPanel.add(category_box[1]);
		categoryJPanel.add(category_box[2]);
		categoryJPanel.add(category_box[3]);
		categoryJPanel.add(category_box[4]);
		categoryJPanel.add(category_box[5]);
		categoryJPanel.add(category_box[6]);

		searchJPanel = new JPanel();
		searchJPanel.add(new JLabel("������"));
		save_tf = new JTextField(20);
		set_btn = new JButton("����");
		search_btn = new JButton("�˻�");
		set_btn.addActionListener(this);
		search_btn.addActionListener(this);

		searchJPanel.add(save_tf);
		searchJPanel.add(set_btn);
		searchJPanel.add(search_btn);

		showJPanel = new JPanel();
		showJPanel.add(new JLabel("�˻��� ��� �� : "));
		count_lb = new JLabel("0");
		showJPanel.add(count_lb);

		contentPane.add(dateJPanel);
		contentPane.add(categoryJPanel);
		contentPane.add(searchJPanel);
		contentPane.add(showJPanel);

		this.setSize(450, 200);
		this.setVisible(true);
	}

	public static void main(String args[]) {
		new MainFrame();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == set_btn) {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.showDialog(this, null);
			File dir = jfc.getSelectedFile();
			save_tf.setText(dir != null ? dir.getPath() : "");
		}

		if (e.getSource() == search_btn) {
			searchFunc();
		}
	}

	public void searchFunc() {
		int before = Integer.parseInt(before_tf.getText());
		int after = Integer.parseInt(after_tf.getText());
		String path = save_tf.getText();
		String url = "http://news.naver.com/main/list.nhn?sid1=100&mid=sec&mode=LSD&date=20140725&page=1";
		if (before > after) {
			JOptionPane.showMessageDialog(null, "��¥�� �Է��� �ùٸ��� �ʽ��ϴ�.", "��¥ ����",
					JOptionPane.ERROR_MESSAGE);
		} else {
			for (int i = before; i <= after; i++) {
				String dayFolder = Integer.toString(i);
				path = save_tf.getText();
				dayFolder = isDirectory(path+"\\"+dayFolder);
				for (int j = 0; j < category_box.length; j++) {
					if (checkCategory(j)) {
						path = isDirectory(dayFolder + "\\" + category_box[j].getText());
						url = "http://news.naver.com/main/list.nhn?sid1="
								+ Integer.toString(j + 100)
								+ "&mid=sec&mode=LSD&date="
								+ Integer.toString(i) + "&page=1";
						System.out.println(url);
						new Thread(new NewsCrawler(url, path)).start();
					}
				}
			}
		}
	}
	
	public String isDirectory(String path){
		File dir = new File(path);
		if(!dir.exists()){
			System.out.println(path + " ������ ����������ϴ�.");
			dir.mkdir();
		}
		return path;
	}
	public boolean checkCategory(int index) {
		if (category_box[index].isSelected()) {
			return true;
		}
		return false;
	}
}
