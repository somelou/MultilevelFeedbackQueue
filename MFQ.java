

/*
 * @program: MFQ
 * @description: MFQSimulation
 * @author: lou
 * @create: 2018-05-20-22-04
 **/


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class MFQ {

    private static JFrame frame = new JFrame("多级反馈队列轮转法");
    private static JPanel processPanel = new JPanel();
    private static JScrollPane scrollPane = new JScrollPane(processPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

    /*    private static JButton createProcess=new JButton("Create A Process");
        private static JButton startMFQ = new JButton("Start Scheduling");
        private static JButton stopMFQ = new JButton("Stop Scheduling");*/
    //菜单组件
    private static JMenuBar menuBar = new JMenuBar();
    private static JMenu processSettingsMenu = new JMenu("Process Settings");
    private static JMenuItem createProcessItem = new JMenuItem("Create A Process");
    private static JMenuItem startMFQItem = new JMenuItem("Start Scheduling");
    private static JMenuItem stopMFQItem = new JMenuItem("Stop Scheduling");

    //记录已经使用的pid
    public static int[] pidUsed = new int[101];

    //当前内存中的进程数
    public static int currentPCBsNum = 0;

    //内存中能够容纳的最大进程数（可分配的pid数）
    public static final int PCB_MAX_NUM = 100;

    //优先级级数
    public static final int PCB_MAX_PRIORITY = 10;

    //设置优先级最高的队列的时间片大小默认值
    public static double timeSlice = 0.3;

    //
    public static double PCBsQueuesTimeSlice[] = new double[PCB_MAX_PRIORITY];

    //多级反馈队列
    public static PCBsQueue[] PCBsQueues = new PCBsQueue[PCB_MAX_PRIORITY];

    //是否停止
    public static boolean isStop;

    //执行窗口初始化
    public void initWindow() {

        //创建菜单栏
        processSettingsMenu.add(createProcessItem);
        processSettingsMenu.addSeparator();
        processSettingsMenu.add(startMFQItem);
        processSettingsMenu.addSeparator();
        processSettingsMenu.add(stopMFQItem);

        menuBar.add(processSettingsMenu);
        frame.setJMenuBar(menuBar);
/*
        frame.getContentPane().setLayout(null);
        createProcess.setBounds(50,50,100,50);
        startMFQ.setBounds(100,50,100,50);
        stopMFQ.setBounds(150,50,100,50);
        frame.getContentPane().add(createProcess);
        frame.getContentPane().add(startMFQ);
        frame.getContentPane().add(stopMFQ);*/

        processPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        frame.setContentPane(scrollPane);
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        this.initPCBs();//初始化pcb
        this.setComponentsListeners();//为控件绑定监听器
    }

    //初始化相关内存参数
    public static void initPCBs() {
        currentPCBsNum = 0;

        Arrays.fill(pidUsed, 1, 101, currentPCBsNum);

        for (int i = 0; i < PCBsQueues.length; i++) {
            PCBsQueues[i] = new PCBsQueue(i);
        }

        for (int i = PCBsQueuesTimeSlice.length - 1; i >= 0; i--) {
            //队列优先级每降一级，时间片增加0.1秒
            PCBsQueuesTimeSlice[i] = timeSlice;
            timeSlice += 0.1;
        }
    }

    public static void main(String args[]) {
        new MFQ().initWindow();
    }

    //给窗口中所有控件绑定监听器
    public static void setComponentsListeners() {

        createProcessItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createProcess();
            }
        });

        startMFQItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMFQ();
            }
        });

        stopMFQItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopMFQ();
            }
        });

    }

    //创建新进程
    public static void createProcess() {
        if (currentPCBsNum == PCB_MAX_NUM) {
            JOptionPane.showMessageDialog(frame, "The current memory space is full so that cannot create a new process！");
        } else {
            currentPCBsNum++;

            int randomPid = 1 + (int) (Math.random() * ((100 - 1) + 1));

            while (pidUsed[randomPid] == 1) {
                randomPid = 1 + (int) (Math.random() * ((100 - 1) + 1));
            }

            pidUsed[randomPid] = 1;

            int randomArrived = (int) (Math.random() * ((5 - 1) + 1));
            double randomLife = 1.0 + (int) (Math.random() * ((5 - 1) + 1));

            PCB pcb = new PCB(randomPid, PCB.STATUS.NotArrived, randomArrived, PCB_MAX_PRIORITY, randomLife);

            LinkedList<PCB> queue = PCBsQueues[PCB_MAX_PRIORITY - 1].getQueue();
            queue.offer(pcb);
            PCBsQueues[PCB_MAX_PRIORITY - 1].setQueue(queue);

            showPCBQueues(PCBsQueues);
        }
    }

    //开始调度
    public static void startMFQ() {
        isStop = false;

        //更新界面操作必须借助多线程来实现
        new Thread(new Runnable() {
            @Override
            public void run() {
                //当前内存中还留有进程未执行
                while (currentPCBsNum != 0 && !isStop) {
                    for (int i = PCBsQueues.length - 1; i >= 0; i--) {
                        LinkedList<PCB> queue = PCBsQueues[i].getQueue();
                        showPCBQueues(PCBsQueues);
                        Collections.sort(queue, Collections.reverseOrder());//升序

                        if (queue.size() > 0) {
                            //读取该队列首个PCB
                            PCB pcb = queue.element();

                            int pid = pcb.getPid();
                            int priority = pcb.getPriority();
                            int arrive = pcb.getArriveTime();
                            double life = pcb.getLifeTime();

                            if (arrive > 0) {
                                //延时到达时间
                                try {
                                    Thread.sleep(arrive * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                for(int j=0;j<queue.size();j++){
                                    queue.get(j).setArriveTime(queue.get(j).getArriveTime()-arrive);
                                }
                            }
                            pcb.setArriveTime(arrive);
                            pcb.setStatus(PCB.STATUS.Ready);
                            showPCBQueues(PCBsQueues);
                            //暂停0.2秒让ready显示
                            try {
                                Thread.sleep((int) (0.2 * 1000));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (priority > 0) {
                                priority--;
                            }
                            life = life - PCBsQueuesTimeSlice[i];
                            life = (double) Math.round(life * 100) / 100;//保留两位小数显示

                            pcb.setStatus(PCB.STATUS.Running);
                            showPCBQueues(PCBsQueues);
                            //通过延时一个时间片来模拟该进程的执行
                            try {
                                Thread.sleep((int) (PCBsQueuesTimeSlice[priority] * 1000));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            //若该进程执行完成
                            if (life <= 0) {
                                //移除该队列的首个PCB
                                pcb.setStatus(PCB.STATUS.Finished);
                                queue.poll();
                                pidUsed[pid] = 0;
                                currentPCBsNum--;
                            }
                            //若该进程还未执行完成,则改变其PCB的相关参数,并插入其优先级所对应的队列尾部
                            else {
                                //移除该队列的首个PCB
                                queue.poll();

                                pcb.setPriority(priority);
                                pcb.setLifeTime(life);
                                pcb.setStatus(PCB.STATUS.Waiting);
                                LinkedList<PCB> nextQueue = PCBsQueues[priority].getQueue();
                                nextQueue.offer(pcb);
                                PCBsQueues[priority].setQueue(nextQueue);
                            }
                            break;
                        }
                    }
                }

                initPCBs();
                showPCBQueues(PCBsQueues);
                //所有进程均执行完成，进程调度完成
                JOptionPane.showMessageDialog(frame, "Process scheduling over!");
            }
        }).start();

    }

    //强制结束进程调度
    public static void stopMFQ() {
        isStop = true;
        initPCBs();
    }

    //显示内存中的多级反馈队列
    public static void showPCBQueues(PCBsQueue[] PCBsQueues) {
        int queueLocationY = 100;
        JPanel queuesPanel = new JPanel();

        for (int i = PCBsQueues.length - 1; i >= 0; i--) {
            LinkedList<PCB> queue = PCBsQueues[i].getQueue();

            if (queue.size() > 0) {
                //创建一个PCB队列
                JPanel PCBsQueue = new JPanel();
                // PCBsQueue.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                PCBsQueue.setLayout(new FlowLayout(FlowLayout.LEFT));
                PCBsQueue.setBounds(0, queueLocationY, 800, 700);

                queueLocationY += 50;

                //创建队列前面的优先级提示块
                JLabel PCBsQueuePriorityLabel = new JLabel("Queue: priority " + String.valueOf(i));
                PCBsQueuePriorityLabel.setOpaque(true);
                PCBsQueuePriorityLabel.setBackground(Color.BLUE);
                PCBsQueuePriorityLabel.setForeground(Color.YELLOW);

                JPanel PCBsQueuePriorityBlock = new JPanel();
                PCBsQueuePriorityBlock.add(PCBsQueuePriorityLabel);

                PCBsQueue.add(PCBsQueuePriorityBlock);

                for (PCB pcb : queue) {

                    //JLabel默认情况下是透明的所以直接设置背景颜色是无法显示的，必须将其设置为不透明才能显示背景

                    //设置pid标签
                    JLabel pidLabel = new JLabel("Pid: " + String.valueOf(pcb.getPid()));
                    pidLabel.setOpaque(true);
                    pidLabel.setBackground(Color.GREEN);
                    pidLabel.setForeground(Color.RED);
                    pidLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    //设置status标签
                    JLabel statusLabel = new JLabel("Status: " + pcb.getStatus());
                    statusLabel.setOpaque(true);
                    statusLabel.setBackground(Color.GREEN);
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    //设置arriveTime标签
                    JLabel priorityLabel = new JLabel("ArriveTime: " + String.valueOf(pcb.getArriveTime()));
                    priorityLabel.setOpaque(true);
                    priorityLabel.setBackground(Color.GREEN);
                    priorityLabel.setForeground(Color.RED);
                    priorityLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    //设置lifeTime标签
                    JLabel lifeLabel = new JLabel("Life: " + String.valueOf(pcb.getLifeTime()));
                    lifeLabel.setOpaque(true);
                    lifeLabel.setBackground(Color.GREEN);
                    lifeLabel.setForeground(Color.RED);
                    lifeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    //绘制一个PCB
                    JPanel PCBPanel = new JPanel();
                    PCBPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    PCBPanel.setBackground(Color.BLUE);
                    PCBPanel.add(pidLabel);
                    PCBPanel.add(statusLabel);
                    PCBPanel.add(priorityLabel);
                    PCBPanel.add(lifeLabel);

                    //将PCB加入队列
                    PCBsQueue.add(new DrawLinePanel());
                    PCBsQueue.add(PCBPanel);
                }

                queuesPanel.add(PCBsQueue);
            }
        }


        //设置queuesPanel中的所有PCB队列（PCBsQueue组件）按垂直方向排列
        BoxLayout boxLayout = new BoxLayout(queuesPanel, BoxLayout.Y_AXIS);
        queuesPanel.setLayout(boxLayout);

        queuesPanel.setSize(800, 700);

        processPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        processPanel.removeAll();
        processPanel.add(queuesPanel);
        processPanel.updateUI();
        processPanel.repaint();
    }

}


//绘制直线类
class DrawLinePanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(0, this.getSize().height / 2, this.getSize().width, this.getSize().height / 2);
    }
}


