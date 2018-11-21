

/*
 * @program: MFQ
 * @description: PCB
 * @author: lou
 * @create: 2018-05-20-22-04
 **/


//进程控制块类
public class PCB implements Comparable<PCB> {
    @Override
    public int compareTo(PCB p) {
        if (this.getArriveTime() < p.getArriveTime()) {
            return 1;
        } else if (this.getArriveTime() > p.getArriveTime()) {
            return -1;
        } else {
            return 0;
        }
    }

    //进程状态枚举
    enum STATUS {
        NotArrived, Waiting,Ready, Running, Finished
    }


    //进程标识符
    private int pid;

    //进程状态标识
    private STATUS status;

    //到达时间
    private int arriveTime;

    //进程优先级
    private int priority;

    //进程生命周期
    private double lifeTime;

    public PCB() {
    }

    public PCB(int pid, STATUS status, int arrive, int priority, double life) {
        this.pid = pid;
        this.status = status;
        this.arriveTime = arrive;
        this.priority = priority;
        this.lifeTime = life;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public int getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(int arriveTime) {
        this.arriveTime = arriveTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(double lifeTime) {
        this.lifeTime = lifeTime;
    }
}
