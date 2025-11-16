//package com.univus.project.service;
//
//import com.univus.project.dto.team.TeamInviteReqDto;
//
//import javax.transaction.Transactional;
//
//public class TeamService {
//    // 초대 생성
//    public Boolean inviteMember(TeamInviteReqDto dto){
//        TeamInvite invite = new TeamInvite();
//        invite.setTeamName(dto.getTeamName());
//        invite.setInvitee(dto.getInvitee());
//        invite.setInviter("로그인 사용자 ID"); // SecurityContext 또는 JWT에서 추출
//
//        inviteRepository.save(invite);
//
//        return true;
//    }
//
//    // 초대 수락
//    @Transactional
//    public Boolean acceptInvite(Long inviteId) {
//
//        TeamInvite invite = inviteRepository.findById(inviteId)
//                .orElseThrow(() -> new RuntimeException("초대가 존재하지 않습니다."));
//
//        if (invite.getStatus() != InviteStatus.PENDING)
//            throw new RuntimeException("이미 처리된 초대입니다.");
//
//        invite.setStatus(InviteStatus.ACCEPTED);
//
//        // 팀 멤버 추가 로직
//        teamMemberRepository.save(
//                new TeamMember(invite.getTeamName(), invite.getInvitee())
//        );
//
//        return true;
//    }
//
//    // 초대 거절
//    @Transactional
//    public Boolean declineInvite(Long inviteId) {
//
//        TeamInvite invite = inviteRepository.findById(inviteId)
//                .orElseThrow(() -> new RuntimeException("초대가 존재하지 않습니다."));
//
//        invite.setStatus(InviteStatus.DECLINED);
//
//        return true;
//    }
//
//}
