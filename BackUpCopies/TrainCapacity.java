package edu.sjsu.cmpe275.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Value;

@Entity
public class TrainCapacity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    public long id;
	
	public String trainNumber;
	
	public Date travelDate;
	
	public String status="Scheduled";
	
	@Column(columnDefinition="int default 5")
	public int A_capacity;
	@Column(columnDefinition="int default 5")
	public int B_capacity;
	@Column(columnDefinition="int default 5")
	public int C_capacity;
	@Column(columnDefinition="int default 5")
	public int D_capacity;
	@Column(columnDefinition="int default 5")
	public int E_capacity;
	@Column(columnDefinition="int default 5")
	public int F_capacity;
	@Column(columnDefinition="int default 5")
	public int G_capacity;
	@Column(columnDefinition="int default 5")
	public int H_capacity;
	@Column(columnDefinition="int default 5")
	public int I_capacity;
	@Column(columnDefinition="int default 5")
	public int J_capacity;
	@Column(columnDefinition="int default 5")
	public int K_capacity;
	@Column(columnDefinition="int default 5")
	public int L_capacity;
	@Column(columnDefinition="int default 5")
	public int M_capacity;
	@Column(columnDefinition="int default 5")
	public int N_capacity;
	@Column(columnDefinition="int default 5")
	public int O_capacity;
	@Column(columnDefinition="int default 5")
	public int P_capacity;
	@Column(columnDefinition="int default 5")
	public int Q_capacity;
	@Column(columnDefinition="int default 5")
	public int R_capacity;
	@Column(columnDefinition="int default 5")
	public int S_capacity;
	@Column(columnDefinition="int default 5")
	public int T_capacity;
	@Column(columnDefinition="int default 5")
	public int U_capacity;
	@Column(columnDefinition="int default 5")
	public int V_capacity;
	@Column(columnDefinition="int default 5")
	public int W_capacity;
	@Column(columnDefinition="int default 5")
	public int X_capacity;
	@Column(columnDefinition="int default 5")
	public int Y_capacity;
	@Column(columnDefinition="int default 5")
	public int Z_capacity;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTrainNumber() {
		return trainNumber;
	}
	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	public Date getTravelDate() {
		return travelDate;
	}
	public void setTravelDate(Date travelDate) {
		this.travelDate = travelDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getA_capacity() {
		return A_capacity;
	}
	public void setA_capacity(int a_capacity) {
		A_capacity = a_capacity;
	}
	public int getB_capacity() {
		return B_capacity;
	}
	public void setB_capacity(int b_capacity) {
		B_capacity = b_capacity;
	}
	public int getC_capacity() {
		return C_capacity;
	}
	public void setC_capacity(int c_capacity) {
		C_capacity = c_capacity;
	}
	public int getD_capacity() {
		return D_capacity;
	}
	public void setD_capacity(int d_capacity) {
		D_capacity = d_capacity;
	}
	public int getE_capacity() {
		return E_capacity;
	}
	public void setE_capacity(int e_capacity) {
		E_capacity = e_capacity;
	}
	public int getF_capacity() {
		return F_capacity;
	}
	public void setF_capacity(int f_capacity) {
		F_capacity = f_capacity;
	}
	public int getG_capacity() {
		return G_capacity;
	}
	public void setG_capacity(int g_capacity) {
		G_capacity = g_capacity;
	}
	public int getH_capacity() {
		return H_capacity;
	}
	public void setH_capacity(int h_capacity) {
		H_capacity = h_capacity;
	}
	public int getI_capacity() {
		return I_capacity;
	}
	public void setI_capacity(int i_capacity) {
		I_capacity = i_capacity;
	}
	public int getJ_capacity() {
		return J_capacity;
	}
	public void setJ_capacity(int j_capacity) {
		J_capacity = j_capacity;
	}
	public int getK_capacity() {
		return K_capacity;
	}
	public void setK_capacity(int k_capacity) {
		K_capacity = k_capacity;
	}
	public int getL_capacity() {
		return L_capacity;
	}
	public void setL_capacity(int l_capacity) {
		L_capacity = l_capacity;
	}
	public int getM_capacity() {
		return M_capacity;
	}
	public void setM_capacity(int m_capacity) {
		M_capacity = m_capacity;
	}
	public int getN_capacity() {
		return N_capacity;
	}
	public void setN_capacity(int n_capacity) {
		N_capacity = n_capacity;
	}
	public int getO_capacity() {
		return O_capacity;
	}
	public void setO_capacity(int o_capacity) {
		O_capacity = o_capacity;
	}
	public int getP_capacity() {
		return P_capacity;
	}
	public void setP_capacity(int p_capacity) {
		P_capacity = p_capacity;
	}
	public int getQ_capacity() {
		return Q_capacity;
	}
	public void setQ_capacity(int q_capacity) {
		Q_capacity = q_capacity;
	}
	public int getR_capacity() {
		return R_capacity;
	}
	public void setR_capacity(int r_capacity) {
		R_capacity = r_capacity;
	}
	public int getS_capacity() {
		return S_capacity;
	}
	public void setS_capacity(int s_capacity) {
		S_capacity = s_capacity;
	}
	public int getT_capacity() {
		return T_capacity;
	}
	public void setT_capacity(int t_capacity) {
		T_capacity = t_capacity;
	}
	public int getU_capacity() {
		return U_capacity;
	}
	public void setU_capacity(int u_capacity) {
		U_capacity = u_capacity;
	}
	public int getV_capacity() {
		return V_capacity;
	}
	public void setV_capacity(int v_capacity) {
		V_capacity = v_capacity;
	}
	public int getW_capacity() {
		return W_capacity;
	}
	public void setW_capacity(int w_capacity) {
		W_capacity = w_capacity;
	}
	public int getX_capacity() {
		return X_capacity;
	}
	public void setX_capacity(int x_capacity) {
		X_capacity = x_capacity;
	}
	public int getY_capacity() {
		return Y_capacity;
	}
	public void setY_capacity(int y_capacity) {
		Y_capacity = y_capacity;
	}
	public int getZ_capacity() {
		return Z_capacity;
	}
	public void setZ_capacity(int z_capacity) {
		Z_capacity = z_capacity;
	}
	
}
